package org.udesa.tp1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftCardTest {

    private static GiftCard newClaimedGiftCardWith100() {
        return new GiftCard(100).claimCard("sarf");
    }
    private static GiftCard newUnclaimedGiftCardWith100() {
        return new GiftCard(100);
    }

    @Test
    public void test01NewGiftCardHasNoExpenses() {
        assertEquals(0, newClaimedGiftCardWith100().expenses().size());
    }

    @Test
    public void test02GiftCardWithBalanceHasBalance() {
        assertEquals(100, newClaimedGiftCardWith100().balance() );
    }

    @Test
    public void test03GiftCardUpdatesBalanceAfterSpending(){
        assertEquals(50, newClaimedGiftCardWith100().chargeGiftCard("Jorf", 50, "The Prancing Pony", LocalDateTime.now()).balance());
    }

    @Test
    public void test04GiftCardThrowsErrorIfNotEnoughMoney(){
        assertThrowsLike( () -> newClaimedGiftCardWith100().chargeGiftCard("Jorf", 150, "The Prancing Pony", LocalDateTime.now()), GiftCard.NotEnoughBalanceError);
    }

    @Test
    public void test05GiftCardCanBeClaimedOnce(){
        assertEquals("sarf", newUnclaimedGiftCardWith100().claimCard("sarf").owner());
    }

    @Test
    public void test06GiftCardCantBeClaimedTwice(){
        assertThrowsLike( () -> newUnclaimedGiftCardWith100().claimCard("sarf").claimCard("jorf"), GiftCard.GiftCardIsClaimedError);
    }

    @Test
    public void test07GiftCardCannotSpendIfUnclaimed(){
        assertThrowsLike( () -> newUnclaimedGiftCardWith100().chargeGiftCard("Jorf", 50, "The Prancing Pony", LocalDateTime.now()), GiftCard.GiftCardIsNotClaimedError);
    }

    @Test
    public void test08AskingForOwnerInUnclaimedCardThrows(){
        assertThrowsLike(() -> newUnclaimedGiftCardWith100().owner(), GiftCard.GiftCardIsNotClaimedError);
    }

    @Test
    public void test09CannotBeChargedIfCardNotOwnedByCorrectOwner(){
        assertThrowsLike(() -> newClaimedGiftCardWith100().chargeGiftCard("Fake user", 50, "The Prancing Pony", LocalDateTime.now()), GiftCard.UserDoesNotOwnCardError);
    }

    @Test
    public void test09TransactionIsAddedAfterCharging(){
        GiftCard giftCard = newClaimedGiftCardWith100();
        giftCard.chargeGiftCard("Jorf", 50, "The Prancing Pony", LocalDateTime.now());
        assertEquals(1, giftCard.expenses().size());
    }

    @Test
    public void test09TransactionHas(){
        GiftCard giftCard = newClaimedGiftCardWith100();
        giftCard.chargeGiftCard("Jorf", 50, "The Prancing Pony", LocalDateTime.now());
        assertEquals(1, giftCard.expenses().size());
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }


}