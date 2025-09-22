package org.udesa.tp1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftCardTest {
    String PaymentDescription1 = "The Prancing Pony";
    LocalDateTime PaymentDate1 = LocalDateTime.now();
    String Person1 = "sarf";
    String Person2 = "Jorf";
    String FakePerson = "Fake user";

    @Test
    public void test01NewGiftCardHasNoExpenses() {
        assertEquals(0, newClaimedGiftCardWith100BySarf().expenses().size());
    }

    @Test
    public void test02GiftCardWithBalanceHasBalance() {
        assertEquals(100, newClaimedGiftCardWith100BySarf().balance() );
    }

    @Test
    public void test03GiftCardUpdatesBalanceAfterSpending(){
        assertEquals(50, chargeClaimedGiftCardWith100BySarf(50).balance());
    }

    @Test
    public void test04GiftCardThrowsErrorIfNotEnoughMoney(){
        assertThrowsLike( () -> chargeClaimedGiftCardWith100BySarf(150), GiftCard.NotEnoughBalanceError);
    }

    @Test
    public void test05GiftCardCanBeClaimedOnce(){
        assertEquals(Person1, newClaimedGiftCardWith100BySarf().owner());
    }

    @Test
    public void test06GiftCardCantBeClaimedTwice(){
        assertThrowsLike( () -> newClaimedGiftCardWith100BySarf().claimCard(Person2), GiftCard.GiftCardIsClaimedError);
    }

    @Test
    public void test07GiftCardCannotSpendIfUnclaimed(){
        assertThrowsLike( () -> newUnclaimedGiftCardWith100().chargeGiftCard(Person2, 50, PaymentDescription1, LocalDateTime.now()), GiftCard.GiftCardIsNotClaimedError);
    }

    @Test
    public void test08AskingForOwnerInUnclaimedCardThrows(){
        assertThrowsLike(() -> newUnclaimedGiftCardWith100().owner(), GiftCard.GiftCardIsNotClaimedError);
    }

    @Test
    public void test09CannotBeChargedIfCardNotOwnedByCorrectOwner(){
        assertThrowsLike(() -> newClaimedGiftCardWith100BySarf().chargeGiftCard(FakePerson, 50, PaymentDescription1, LocalDateTime.now()), GiftCard.UserDoesNotOwnCardError);
    }

    @Test
    public void test09TransactionIsAddedAfterCharging(){
        assertEquals(1, chargeClaimedGiftCardWith100BySarf(50).expenses().size());
    }

    @Test
    public void test10TransactionSavesAccurateInformation(){
        assertEquals(new Transaction(100, PaymentDate1, PaymentDescription1), chargeClaimedGiftCardWith100BySarf(100).expenses().get(0));
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }

    private GiftCard newUnclaimedGiftCardWith100() {
        return new GiftCard(100);
    }
    private GiftCard newClaimedGiftCardWith100BySarf() {
        return newUnclaimedGiftCardWith100().claimCard(Person1);
    }
    private GiftCard chargeClaimedGiftCardWith100BySarf(float amount) {
        return newClaimedGiftCardWith100BySarf().chargeGiftCard(Person1, amount, PaymentDescription1, PaymentDate1);
    }


}