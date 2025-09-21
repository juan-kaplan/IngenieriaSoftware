package org.udesa.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SystemFacadeTest {
    public static String giftCardId1 = "CardWith100";
    public static String giftCardId2 = "CardWith200";
    public static String invalidGiftCardId = "CardWith0";

    SystemFacade systemFacade;
    GiftCard giftCard1;
    GiftCard giftCard2;
    @BeforeEach
    public void beforeEach() { systemFacade = systemFacade();}

    @Test
    public void test01SystemAcceptsValidUserLogin() {
        assertTrue(systemFacade.isTokenValid(systemFacade.login("John", "Jpass")));
    }

    @Test
    public void test02SystemRejectsInvalidUserLogin() {
        assertThrowsLike( () -> systemFacade.login( "Sarf", "ImAHacker!!"), SystemFacade.InvalidLoginCredentialsError);
    }


    @Test
    public void test03AcceptsSeveralValidUsers() {
        verifyTokenBeforeAndAfter(1, "John", "Jpass");
        verifyTokenBeforeAndAfter(2, "Paul", "Ppass");
    }

    @Test
    public void test03ValidUserClaimsValidGiftCard(){
        systemFacade.claimGiftCard(systemFacade.login("John", "Jpass"), giftCardId1);
        assertEquals("John", giftCard1.owner());
    }

    @Test
    public void test04ValidUserClaimsInvalidGiftCard(){
        assertThrowsLike(()-> systemFacade.claimGiftCard(systemFacade.login("John", "Jpass"), invalidGiftCardId), SystemFacade.InvalidGiftCardSelectedError);
    }

    @Test
    public void test05ValidUserClaimsAlreadyClaimedGiftCard(){
        systemFacade.claimGiftCard(systemFacade.login("John", "Jpass"), giftCardId1);
        assertThrowsLike(()-> systemFacade.claimGiftCard(systemFacade.login("Paul", "Ppass"), giftCardId1), GiftCard.GiftCardIsClaimedError);
    }

    @Test
    public void test06ValidUserChecksBalanceOfGiftCard(){
        Integer token = systemFacade.login("John", "Jpass");
        assertEquals(100, systemFacade.claimGiftCard(token, giftCardId1).checkGiftCardBalance(token, giftCardId1));
    }

    @Test
    public void test07ValidMerchantCanChargeCardOwnedByValidUser(){
        userClaimsGiftCard("John", "Jpass", giftCardId1);
        assertEquals(20, systemFacade.chargeUsersGiftCard("Restaurant1", giftCardId1, "John", 80).validGiftCards().findById(giftCardId1).balance());
    }

    @Test
    public void test08ValidMerchantCannotChargeInvalidUser(){
        assertThrowsLike( () -> systemFacade.chargeUsersGiftCard("Restaurant1",giftCardId1, "Sarf", 80), SystemFacade.InvalidUserNameError);
    }

    @Test
    public void test09ValidMerchantCannotChargeInvalidGiftCard(){
        assertThrowsLike( () -> systemFacade.chargeUsersGiftCard("Restaurant1","Fake card", "John", 80), SystemFacade.InvalidGiftCardSelectedError);
    }

    @Test
    public void test10InvalidMerchantCannotCharge(){
        assertThrowsLike( () -> systemFacade.chargeUsersGiftCard("Fake store",giftCardId1, "John", 80), SystemFacade.InvalidMerchantError);
    }

    @Test
    public void test11ValidMerchantCannotChargeValidCardNotOwnedByValidUser(){
        userClaimsGiftCard("Paul", "Ppass", giftCardId1);
        assertThrowsLike( () -> systemFacade.chargeUsersGiftCard("Restaurant1",giftCardId1, "John", 80), SystemFacade.UserDoesNotOwnCardError);

    }

    @Test
    public void test12TransactionAddedToCardAfterMerchantChargesCorrectly(){
        userClaimsGiftCard("John", "Jpass", giftCardId1);
        systemFacade.chargeUsersGiftCard("Restaurant1", giftCardId1, "John", 80);
        assertEquals(1, getCardExpenses(giftCardId1).size());
        assertEquals("Restaurant1", getCardExpenses(giftCardId1).get(0).merchantKey());
        assertEquals(80, getCardExpenses(giftCardId1).get(0).amount());
        assertEquals("????", getCardExpenses(giftCardId1).get(0).description());
    }

    private List<Transaction> getCardExpenses(String giftCardId) {
        return systemFacade.validGiftCards().findById(giftCardId).expenses();
    }


    private void userClaimsGiftCard(String user, String password, String giftCardId) {
        Integer token = systemFacade.login(user, password);
        systemFacade.claimGiftCard(token, giftCardId);
    }

    private SystemFacade systemFacade() {
        giftCard1 = new GiftCard(100, giftCardId1);
        giftCard2 = new GiftCard(250, giftCardId2);
        User user1 = new User("John", "Jpass");
        User user2 = new User("Paul", "Ppass");
        Merchant merchant1 = new Merchant("Restaurant1", "The Prancing Pony");
        Merchant merchant2 = new Merchant("Store1", "Nike");

        return new SystemFacade(new Repository<User>().saveItem(user1).saveItem(user2),
                                new Repository<Merchant>().saveItem(merchant1).saveItem(merchant2),
                                new Repository<GiftCard>().saveItem(giftCard1).saveItem(giftCard2),
                                new Clock()
                                );
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }

    private void verifyTokenBeforeAndAfter(int token, String user, String pass) {
        assertFalse(systemFacade.isTokenValid(token));
        systemFacade.login(user, pass);
        assertTrue(systemFacade.isTokenValid(token));
    }

}
