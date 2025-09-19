package org.udesa.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SystemFacadeTest {

    SystemFacade systemFacade;
    GiftCard giftCard100;
    GiftCard giftCard250;
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
        systemFacade.claimGiftCard(systemFacade.login("John", "Jpass"), 100);
        assertEquals("John", giftCard100.owner());
    }

    @Test
    public void test04ValidUserClaimsInvalidGiftCard(){
        assertThrowsLike(()-> systemFacade.claimGiftCard(systemFacade.login("John", "Jpass"), 0), SystemFacade.InvalidGiftCardSelectedError);
    }

    @Test
    public void test05ValidUserClaimsAlreadyClaimedGiftCard(){
        systemFacade.claimGiftCard(systemFacade.login("John", "Jpass"), 100);
        assertThrowsLike(()-> systemFacade.claimGiftCard(systemFacade.login("Paul", "Ppass"), 100), GiftCard.GiftCardIsClaimedError);
    }

    @Test
    public void test06ValidUserChecksBalanceOfGiftCard(){
        Integer token = systemFacade.login("John", "Jpass");
        assertEquals(100, systemFacade.claimGiftCard(token, 100).checkBalance(token, 100));
    }

    private SystemFacade systemFacade() {
        giftCard100 = new GiftCard(100);
        giftCard250 = new GiftCard(250);

        return new SystemFacade(Map.of( "John", "Jpass", "Paul", "Ppass" ),
                                Map.of(100 , giftCard100, 250, giftCard250),
                                new Clock());
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}
