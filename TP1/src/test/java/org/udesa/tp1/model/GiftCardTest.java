package org.udesa.tp1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftCardTest {

    private static GiftCard newClaimedGiftCardWith100() {
        return new GiftCard(100, "CARDWITH100").claimCard("sarf");
    }
    private static GiftCard newUnclaimedGiftCardWith100() {
        return new GiftCard(100, "CARDWITH100");
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
        assertEquals(50, newClaimedGiftCardWith100().spend(50).balance());
    }

    @Test
    public void test04GiftCardThrowsErrorIfNotEnoughMoney(){
        assertThrowsLike( () -> newClaimedGiftCardWith100().spend(150), GiftCard.NotEnoughBalanceError);
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
        assertThrowsLike( () -> newUnclaimedGiftCardWith100().spend(50), GiftCard.GiftCardIsNotClaimedError);
    }

    @Test
    public void test08AskingForOwnerInUnclaimedCardThrows(){
        assertThrowsLike(() -> newUnclaimedGiftCardWith100().owner(), GiftCard.GiftCardIsNotClaimedError);
    }

    @Test
    public void test09TransactionCanBeAdded(){
        GiftCard giftCard = newClaimedGiftCardWith100();
        giftCard.addTransaction(50,  LocalDateTime.now(), "No description");
        assertEquals(transaction, giftCard.expenses().get(0));
    }
    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }


}
