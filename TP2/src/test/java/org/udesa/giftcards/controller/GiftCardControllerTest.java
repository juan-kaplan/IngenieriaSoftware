package org.udesa.giftcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.giftcards.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class GiftCardControllerTest {

    public static Random randomStream = new Random(Instant.now().getEpochSecond());

    @Autowired MockMvc mockMvc;
    @Autowired GiftCardController controller;

    @Autowired UserService userService;
    @Autowired GiftCardService giftCardService;
    @Autowired MerchantService merchantService;

    @MockBean Clock clock;

    @BeforeEach
    public void beforeEach() {
        when(clock.now()).then(it -> LocalDateTime.now());
    }

    @AfterAll
    public void cleanDatabase() {
        userService.deleteByNameStartingWith( "JohnPork" );
        giftCardService.deleteByCardIdStartingWith( "GC" );
        merchantService.deleteByNameStartingWith( "Merchant" );
    }

    private int nextKey() {
        return randomStream.nextInt();
    }

    private UserVault savedUser() {
        return userService.save(new UserVault("JhonPork" + nextKey(), "Jpass"));
    }

    private GiftCard savedCard(int balance) {
        return giftCardService.save(new GiftCard("GC" + nextKey(), balance));
    }

    private Merchant savedMerchant() {
        return merchantService.save(new Merchant("Merchant" + nextKey()));
    }

    @Test
    public void userCanOpenASession() throws Exception {
        assertNotNull(login(savedUser()));
    }

    @Test
    public void unknownUserCannotOpenASession() throws Exception {
        loginFailing("Stuart", "StuPass");
    }

    @Test
    public void userCannotUseAnInvalidToken() throws Exception {
        GiftCard card = savedCard(10);
        String randomToken = UUID.randomUUID().toString();

        redeemFailing(randomToken, card.getCardId());
        balanceFailing(randomToken, card.getCardId());
        detailsFailing(randomToken, card.getCardId());
    }

    @Test
    public void userCannotCheckOnAlienCard() throws Exception {
        GiftCard card = savedCard(10);

        UUID ownerToken = login(savedUser());
        redeem(ownerToken, card.getCardId());

        UUID otherToken = login(savedUser());

        balanceFailing(otherToken.toString(), card.getCardId());
        detailsFailing(otherToken.toString(), card.getCardId());
    }

    @Test
    public void userCanRedeemACard() throws Exception {
        GiftCard card = savedCard(10);
        UUID token = newUserRedeemsCard(card);
        assertEquals(10, balance(token, card.getCardId()));

    }

    @Test
    public void userCanRedeemASecondCard() throws Exception {
        GiftCard card1 = savedCard(10);
        GiftCard card2 = savedCard(5);
        UUID token = newUserRedeemsCard(card1);

        redeem(token, card2.getCardId());

        assertEquals(10, balance(token, card1.getCardId()));
        assertEquals(5, balance(token, card2.getCardId()));
    }

    @Test
    public void userCannotRedeemRedeemedCard() throws Exception {
        GiftCard card = savedCard(10);
        UUID token = newUserRedeemsCard(card);

        redeemFailing(token.toString(), card.getCardId());
    }

    @Test
    public void multipleUsersCanRedeemACard() throws Exception {
        GiftCard card1 = savedCard(10);
        UUID token1 = newUserRedeemsCard(card1);
        assertEquals(10, balance(token1, card1.getCardId()));

        GiftCard card2 = savedCard(5);
        UUID token2 = newUserRedeemsCard(card2);
        assertEquals(5, balance(token2, card2.getCardId()));
    }

    @Test
    public void unknownMerchantCantCharge() throws Exception {
        GiftCard card = savedCard(10);

        chargeFailing("Mx", card.getCardId(), 2, "UnCargo");
    }

    @Test
    public void merchantCantChargeUnredeemedCard() throws Exception {
        GiftCard card = savedCard(10);
        Merchant merchant = savedMerchant();

        chargeFailing(merchant.getName(), card.getCardId(), 2, "UnCargo");
    }

    @Test
    public void merchantCanChargeARedeemedCard() throws Exception {
        GiftCard card = savedCard(10);
        Merchant merchant = savedMerchant();
        UUID token = newUserRedeemsCard(card);

        charge(merchant.getName(), card.getCardId(), 2, "UnCargo");

        assertEquals(8, balance(token, card.getCardId()));
    }

    @Test
    public void merchantCannotOverchargeACard() throws Exception {
        GiftCard card = savedCard(10);
        Merchant merchant = savedMerchant();
        newUserRedeemsCard(card);

        chargeFailing(merchant.getName(), card.getCardId(), 11, "UnCargo");
    }

    @Test
    public void userCanCheckHisEmptyCharges() throws Exception {
        GiftCard card = savedCard(10);
        UUID token = newUserRedeemsCard(card);

        assertTrue(details(token, card.getCardId()).isEmpty());
    }

    @Test
    public void userCanCheckHisCharges() throws Exception {
        GiftCard card = savedCard(10);
        Merchant merchant = savedMerchant();
        UUID token = newUserRedeemsCard(card);

        charge(merchant.getName(), card.getCardId(), 2, "UnCargo");

        List<String> d = details(token, card.getCardId());
        assertEquals("UnCargo", d.getLast());
    }

    @Test
    public void userCannotCheckOthersCharges() throws Exception {
        GiftCard card = savedCard(10);

        UUID ownerToken = login(savedUser());
        redeem(ownerToken, card.getCardId());

        UUID otherToken = login(savedUser());

        detailsFailing(otherToken.toString(), card.getCardId());
    }

    @Test
    public void tokenExpires() throws Exception {
        GiftCard card = savedCard(10);

        when(clock.now())
                .thenReturn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(16));

        UUID token = login(savedUser());

        redeemFailing(token.toString(), card.getCardId());
    }
    private UUID newUserRedeemsCard(GiftCard card) throws Exception {
        UUID token = login(savedUser());
        redeem(token, card.getCardId());
        return token;
    }

    private UUID login(UserVault user) throws Exception {
        return login(user.getName(), user.getPassword());
    }

    private UUID login(String userName, String password) throws Exception {
        return UUID.fromString(
                new ObjectMapper()
                        .readTree(
                                mockMvc.perform(post("/login")
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                .param("user", userName)
                                                .param("pass", password))
                                        .andDo(print())
                                        .andExpect(status().is(200))
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString()
                        )
                        .get("token")
                        .asText()
        );
    }

    private void redeem(UUID token, String cardId) throws Exception {
        mockMvc.perform(post("/" + cardId + "/redeem")
                        .header("Authorization", token.toString()))
                .andDo(print())
                .andExpect(status().is(200));
    }

    private int balance(UUID token, String cardId) throws Exception {
        return new ObjectMapper()
                .readTree(
                        mockMvc.perform(get("/" + cardId + "/balance")
                                        .header("Authorization", token.toString()))
                                .andDo(print())
                                .andExpect(status().is(200))
                                .andReturn()
                                .getResponse()
                                .getContentAsString()
                )
                .get("balance")
                .asInt();
    }


    @SuppressWarnings("unchecked")
    private List<String> details(UUID token, String cardId) throws Exception {
        Map<String, Object> response =
                new ObjectMapper()
                        .readValue(
                                mockMvc.perform(get("/" + cardId + "/details")
                                                .header("Authorization", token.toString()))
                                        .andDo(print())
                                        .andExpect(status().is(200))
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(),
                                Map.class
                        );

        return (List<String>) response.get("details");
    }

    private void charge(String merchantName, String cardId, int amount, String description) throws Exception {
        mockMvc.perform(post("/" + cardId + "/charge")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("merchant", merchantName)
                        .param("amount", Integer.toString(amount))
                        .param("description", description))
                .andDo(print())
                .andExpect(status().is(200));
    }

    private void loginFailing(String user, String pass) throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("user", user)
                        .param("pass", pass))
                .andDo(print())
                .andExpect(status().is(500));
    }

    private void redeemFailing(String token, String cardId) throws Exception {
        mockMvc.perform(post("/" + cardId + "/redeem")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().is(500));
    }

    private void balanceFailing(String token, String cardId) throws Exception {
        mockMvc.perform(get("/" + cardId + "/balance")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().is(500));
    }

    private void detailsFailing(String token, String cardId) throws Exception {
        mockMvc.perform(get("/" + cardId + "/details")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().is(500));
    }

    private void chargeFailing(String merchantName, String cardId, int amount, String description) throws Exception {
        mockMvc.perform(post("/" + cardId + "/charge")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("merchant", merchantName)
                        .param("amount", Integer.toString(amount))
                        .param("description", description))
                .andDo(print())
                .andExpect(status().is(500));
    }

}
