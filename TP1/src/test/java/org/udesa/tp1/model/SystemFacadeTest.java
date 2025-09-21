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
    public void beforeEach() { systemFacade = systemFacade();}

    @Test
    public void test01SystemAcceptsValidUser() {
        assertTrue(systemFacade.isTokenValid(getValidToken("John", "Jpass")));
    }

    @Test
    public void test02SystemRejectsInvalidUser() {
        assertThrowsLike( () -> systemFacade.login( "", ""), SystemFacade.InvalidLoginCredentialsError);
    }

    @Test
    public void test03ValidUserClaimsValidGiftCard(){
        systemFacade.claimGiftCard(getValidToken("John", "Jpass"), giftCardId1);
        assertEquals("John", giftCard1.owner());
    }

    @Test
    public void test04ValidUserClaimsInvalidGiftCard(){
        assertThrowsLike(()-> systemFacade.claimGiftCard(getValidToken("John", "Jpass" ), invalidGiftCardId), SystemFacade.InvalidGiftCardSelectedError);
    }

    @Test
    public void test05ValidUserClaimsAlreadyClaimedGiftCard(){
        assertThrowsLike(()-> systemFacade.claimGiftCard(getValidToken("John", "Jpass"), giftCardId1)
                                            .claimGiftCard(getValidToken("Paul", "Ppass"), giftCardId1), GiftCard.GiftCardIsClaimedError);
    }

    @Test
    public void test06ValidUserChecksBalanceOfGiftCard(){
        Integer token = getValidToken("John", "Jpass");
        assertEquals(100, systemFacade.claimGiftCard(token, giftCardId1).checkBalance(token, giftCardId1));
    }
    
    @Test
    public void test07ValidUserChecksBalanceOfInvalidGiftCard(){
        assertThrowsLike(()-> systemFacade.checkBalance(getValidToken("John", "Jpass"), invalidGiftCardId), SystemFacade.InvalidGiftCardSelectedError);
    }

    @Test
    public void test08InvalidTokenClaimsGiftCard(){
        assertThrowsLike(()-> systemFacade.claimGiftCard(-1, giftCardId1), SystemFacade.InvalidSessionError);
    }

    @Test
    public void test09InvalidTokenChecksBalanceGiftCard(){
        assertThrowsLike(()-> systemFacade.checkBalance(-1, giftCardId1), SystemFacade.InvalidSessionError);
    }

    @Test
    public void test10UserSessionBecomesInvalidAfter5minutes(){
        assertFalse(systemFacadeWithModifiedClock().isTokenValid(getValidToken("John", "Jpass")));
    }

    @Test
    public void test11UserCanNotClaimAfter5minutesInactive(){
        assertThrowsLike(() -> systemFacadeWithModifiedClock()
                .claimGiftCard(getValidToken("John", "Jpass"), giftCardId1),  SystemFacade.InvalidSessionError);
    }

    @Test
    public void test12UserCanNotCheckBalanceAfter5minutesInactive(){
        assertThrowsLike(() -> systemFacadeWithModifiedClock()
                .checkBalance(getValidToken("John", "Jpass"), giftCardId1), SystemFacade.InvalidSessionError);
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

        return new SystemFacade(Map.of( "John", "Jpass", "Paul", "Ppass" ),
                Map.of(giftCardId1 , giftCard1, giftCardId2, giftCard2),
                List.of("Store1", "Store2", "Store3"),
                clock);
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }

    private Integer getValidToken(String user, String password) {
        return systemFacade.login(user, password);
    }
}