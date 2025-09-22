package org.udesa.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SystemFacadeTest {
    public static Integer giftCardId1 = 100;
    public static Integer giftCardId2 = 250;
    public static Integer invalidGiftCardId = 0;

    SystemFacade systemFacade;
    GiftCard giftCard1;
    GiftCard giftCard2;

    @BeforeEach
    public void beforeEach() {
        systemFacade = systemFacade();
    }

    @Test
    public void test01SystemAcceptsValidUser() {
        assertTrue(systemFacade.isTokenValid(logUserIn("John", "Jpass")));
    }

    @Test
    public void test02SystemRejectsInvalidUser() {
        assertThrowsLike(() -> logUserIn("", ""), SystemFacade.InvalidLoginCredentialsError);
    }

    @Test
    public void test03AcceptsSeveralValidUsers() {
        logUserIn("John", "Jpass");
        assertTrue(systemFacade.isTokenValid(logUserIn("Paul", "Ppass")));
    }

    @Test
    public void test04ValidUserClaimsValidGiftCard() {
        systemFacadeWithClaimedCard(giftCardId1,"John" ,"Jpass");
        assertEquals("John", giftCard1.owner());
    }

    @Test
    public void test05ValidUserClaimsInvalidGiftCard() {
        assertInvalidGiftCard(() -> systemFacadeWithClaimedCard(invalidGiftCardId,"John" ,"Jpass"));
    }

    @Test
    public void test06ValidUserClaimsAlreadyClaimedGiftCard() {
        assertThrowsLike(() -> systemFacadeWithClaimedCard(giftCardId1,"John" ,"Jpass")
                .claimGiftCard(logUserIn("Paul", "Ppass"), giftCardId1), GiftCard.GiftCardIsClaimedError);
    }

    @Test
    public void test07ValidUserChecksBalanceOfGiftCard() {
        assertEquals(100, checkBalanceOfCardInFacadeWithClaimedCard(giftCardId1));
    }

    @Test
    public void test08ValidUserChecksBalanceOfInvalidGiftCard() {
        assertInvalidGiftCard(() -> checkBalanceOfCardInFacadeWithClaimedCard(invalidGiftCardId));
    }

    @Test
    public void test09ValidMerchantCanChargeCardOwnedByValidUser() {
        int token = getTokenForFacadeWithClaimedCardId1ByJohn();
        assertEquals(20, systemFacade
                .chargeUsersGiftCard("Merchant1", giftCardId1, "John", 80, "The Prancing Pony")
                .checkBalance(token, giftCardId1));
    }

    @Test
    public void test10ValidMerchantCannotChargeInvalidUser(){
        assertThrowsLike( () -> chargeGiftCardInSystemFacadeWithClaimedCard("Merchant1", giftCardId1, "Absent User", 80, "The Prancing Pony"),
                SystemFacade.InvalidUserNameError);
    }
    @Test
    public void test11ValidMerchantCannotChargeInvalidGiftCard(){
        assertInvalidGiftCard(() -> chargeGiftCardInSystemFacadeWithClaimedCard("Merchant1", invalidGiftCardId, "John", 80, "The Prancing Pony"));
    }

    @Test
    public void test12InvalidMerchantCannotCharge(){
        assertInvalidMerchant( () -> chargeGiftCardInSystemFacadeWithClaimedCard("Fake Merchant", giftCardId1, "John", 80, "The Prancing Pony"));
    }

    @Test
    public void test13InvalidTokenClaimsGiftCard() {
        assertInvalidSession(() -> systemFacade.claimGiftCard(-1, giftCardId1));
    }

    @Test
    public void test14InvalidTokenChecksBalanceGiftCard() {
        assertInvalidSession(() -> systemFacade.checkBalance(-1, giftCardId1));
    }

    @Test
    public void test15InvalidTokenChecksExpences() {
        assertInvalidSession(() -> systemFacade.checkExpenses(-1, giftCardId1));
    }

    @Test
    public void test16UserSessionBecomesInvalidAfter5minutes() {
        assertFalse(systemFacadeWithModifiedClock().isTokenValid(logUserIn("John", "Jpass")));
    }

    @Test
    public void test17UserCanNotClaimAfter5minutesInactive() {
        assertInvalidSession(() -> systemFacadeWithModifiedClock()
                .claimGiftCard(logUserIn("John", "Jpass"), giftCardId1));
    }

    @Test
    public void test18UserCanNotCheckBalanceAfter5minutesInactive() {
        assertInvalidSession(() -> systemFacadeWithModifiedClock()
                .checkBalance(logUserIn("John", "Jpass"), giftCardId1));
    }

    @Test
    public void test19UserCanNotCheckExpensesAfter5minutesInactive() {
        assertInvalidSession(() -> systemFacadeWithModifiedClock()
                .checkExpenses(logUserIn("John", "Jpass"), giftCardId1));
    }


    @Test
    public void test20ValidUserChecksGiftCardExpenses(){
        Integer token = getTokenForFacadeWithClaimedCardId1ByJohn();
        systemFacade.chargeUsersGiftCard("Merchant1", giftCardId1, "John", 50, "Nike");
        assertEquals(1, systemFacade.checkExpenses(token, giftCardId1).size());
    }

    @Test
    public void test21ValidUserChecksExpensesOfInvalidGiftCard(){
        assertInvalidGiftCard( () -> systemFacade.checkExpenses(getTokenForFacadeWithClaimedCardId1ByJohn(), invalidGiftCardId));
    }

    private SystemFacade systemFacade() {
        return systemFacadeClockSkeleton(new Clock());
    }

    private SystemFacade systemFacadeWithModifiedClock() {
        return systemFacadeClockSkeleton(
                new Clock() {
                    public LocalDateTime now() {
                        return LocalDateTime.now().plusMinutes(6);
                    }
                });
    }

    private SystemFacade systemFacadeClockSkeleton(Clock clock) {
        giftCard1 = new GiftCard(100);
        giftCard2 = new GiftCard(250);

        return new SystemFacade(Map.of("John", "Jpass", "Paul", "Ppass"),
                Map.of(giftCardId1, giftCard1, giftCardId2, giftCard2),
                List.of("Merchant1", "Merchant2", "Merchant3"),
                clock);
    }

    private void assertThrowsLike(Executable executable, String message) {
        assertEquals(message,
                assertThrows(Exception.class, executable)
                        .getMessage());
    }

    private Integer logUserIn(String user, String password) {
        return systemFacade.login(user, password);
    }
    private SystemFacade systemFacadeWithClaimedCard(Integer giftCardId, String user, String password){
        return systemFacade.claimGiftCard(logUserIn(user, password), giftCardId);
    }
    private int getTokenForFacadeWithClaimedCardId1ByJohn() {
        int token = logUserIn("John", "Jpass");
        systemFacade.claimGiftCard(token, giftCardId1);
        return token;
    }
    private SystemFacade chargeGiftCardInSystemFacadeWithClaimedCard(String merchant, Integer giftCardId, String user, float amount, String description) {
        return systemFacadeWithClaimedCard(giftCardId1, "John", "Jpass")
                .chargeUsersGiftCard(merchant, giftCardId, user, amount, description);
    }
    private void assertInvalidSession(Executable e) {
        assertThrowsLike(e, SystemFacade.InvalidSessionError);
    }
    private void assertInvalidGiftCard(Executable e) {
        assertThrowsLike(e, SystemFacade.InvalidGiftCardSelectedError);
    }
    private void assertInvalidMerchant(Executable e) {
        assertThrowsLike(e, SystemFacade.InvalidMerchantError);
    }
    private float checkBalanceOfCardInFacadeWithClaimedCard(Integer giftCardId) {
        return systemFacade.checkBalance(getTokenForFacadeWithClaimedCardId1ByJohn(), giftCardId);
    }
}

