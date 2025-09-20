package org.udesa.tp1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class GiftCardRepositoryTest {

    private GiftCard newGiftCardWith100() {
        return new GiftCard(100, "CARDWITH100");
    }

    private GiftCardRepository giftCardRepositoryWithCardWith100(){
        return new GiftCardRepository().saveCard(newGiftCardWith100());
    }


    @Test
    public void testCanFindGiftCardById() {
        assertEquals("CARDWITH100", giftCardRepositoryWithCardWith100().findById("CARDWITH100").giftCardId());
    }

    @Test
    public void testCanSaveGiftCard() {
        GiftCard giftCard = new GiftCard(101, "S4RF1N1T");
       assertEquals(giftCard, giftCardRepositoryWithCardWith100().saveCard(giftCard).findById("S4RF1N1T"));
    }

    @Test
    public void testFindUnknownReturnsEmpty() {
        assertThrowsLike( () -> giftCardRepositoryWithCardWith100().findById("Im not in repository"), GiftCardRepository.CardNotInRepositoryError);

    }

    @Test
    public void testSaveDuplicateThrows(){
        assertThrowsLike( () -> giftCardRepositoryWithCardWith100().saveCard(newGiftCardWith100()), GiftCardRepository.CardAlreadyInRepositoryError);

    }

    @Test
    public void testRemoveCardFromRepository(){
        assertThrowsLike( () -> giftCardRepositoryWithCardWith100().removeCard("CARDWITH100").findById("CARDWITH100"), GiftCardRepository.CardNotInRepositoryError);
    }

    @Test
    public void testCannotRemoveCardThatIsNotPresent(){
        assertThrowsLike( () -> giftCardRepositoryWithCardWith100().removeCard("Im not in repository"),  GiftCardRepository.CardNotInRepositoryError);
    }

    @Test
    public void testVerifyPresenceOfGiftCardInRepository(){
        assertTrue(giftCardRepositoryWithCardWith100().existsById("CARDWITH100"));
    }

    @Test
    public void testVerifyAbsenceOfGiftCardInRepository(){
        assertFalse(giftCardRepositoryWithCardWith100().existsById("Im not in repository"));
    }


    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}
