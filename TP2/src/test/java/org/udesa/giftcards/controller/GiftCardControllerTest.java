package org.udesa.giftcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.giftcards.model.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc
public class GiftCardControllerTest {
    public static Random randomStream = new Random( Instant.now().getEpochSecond() );

    @Autowired MockMvc mockMvc;
    @Autowired GiftCardController controller;
    @Autowired UserService userService;
    @Autowired GiftCardService giftCardService;
    @Autowired MerchantService merchantService;
    @MockBean Clock clock;

    @BeforeEach
    public void beforeEach() {
        when( clock.now() ).then( it -> LocalDateTime.now() );
    }

    private UserVault savedUser() {
        return userService.save( new UserVault( "JhonPork" + nextKey(), "Jpass" ) );
    }
    private GiftCard savedCard( int balance ) { return giftCardService.save( new GiftCard( "GC" + nextKey(), balance ) ); }
    private Merchant savedMerchant() { return merchantService.save( new Merchant( "Merchant" + nextKey() ));}
    private int nextKey() {
        return randomStream.nextInt();
    }

    @Test public void userCanOpenASession() throws Exception {
        assertNotNull( login( savedUser() ));
    }

    private UUID login(UserVault user ) throws Exception {
        return login( user.getName(), user.getPassword() );
    }

    private UUID login(String userName, String password) throws Exception{
        return UUID.fromString(
                new ObjectMapper()
                        .readTree(
                                mockMvc.perform(post("/login")
                                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                .param("user", userName)
                                                .param("pass", password))
                                    .andDo(print())
                                    .andExpect(status().isOk())
                                    .andReturn()
                                    .getResponse()
                                    .getContentAsString()
                        )
                        .get("token")
                        .asText()
        );
    }
}

