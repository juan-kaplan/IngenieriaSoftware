package org.udesa.tp1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftCardTest {

    private static GiftCard newGiftCardWith100() {
        return new GiftCard(100);
    }

    @Test
    public void test01NewGiftCardHasNoExpenses() {
        assertEquals("", newGiftCardWith100().expenses());
    }

    @Test
    public void test02GiftCardWithBalanceHasBalance() {
        assertEquals(100, newGiftCardWith100().balance() );
    }

    @Test
    public void test03GiftCardUpdatesBalanceAfterSpending(){
        assertEquals(50, newGiftCardWith100().spend(50).balance());
    }

    @Test
    public void test04GiftCardThrowsErrorIfNotEnoughMoney(){
        assertThrowsLike( () -> newGiftCardWith100().spend(150), GiftCard.NotEnoughBalanceErrorDescription );
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }

}
