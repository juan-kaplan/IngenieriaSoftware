package org.udesa.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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
        assertTrue(systemFacade.isTokenValid(systemFacade.login("John", "Jpass")));
    }

    @Test
    public void test02SystemRejectsInvalidUser() {
        assertThrowsLike( () -> systemFacade.login( "", ""), SystemFacade.InvalidLoginCredentialsError);
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
        assertEquals(100, systemFacade.claimGiftCard(token, giftCardId1).checkBalance(token, giftCardId1));
    }

    private SystemFacade systemFacade() {
        giftCard1 = new GiftCard(100);
        giftCard2 = new GiftCard(250);

        return new SystemFacade(Map.of( "John", "Jpass", "Paul", "Ppass" ),
                Map.of(giftCardId1 , giftCard1, giftCardId2, giftCard2),
                List.of("Store1", "Store2", "Store3"),
                new Clock());
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}