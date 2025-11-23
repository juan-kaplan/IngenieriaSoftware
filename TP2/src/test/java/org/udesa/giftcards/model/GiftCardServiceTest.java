package org.udesa.giftcards.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GiftCardServiceTest extends ModelServiceTest<GiftCard, GiftCardService> {

    @AfterAll
    public void cleanUp(){
        service.deleteByCardIdStartingWith( EntityDrawer.GiftCardPrefix );
    }

    protected GiftCard newSample() {
        return EntityDrawer.someCard( 10 );
    }

    private GiftCard savedCard( int balance ) { return service.save( EntityDrawer.someCard( balance ) ); }

    protected GiftCard updateSample(GiftCard sample) {
        sample.setCardId("Test_GC");
        return sample;
    }

    @Test public void testFindByCardId() {
        GiftCard aCard = savedCard(10);
        assertEquals( aCard,  service.findByCardId( aCard.getCardId() ));
    }

    @Test public void testDeleteByCardIdStartingWith(){
        GiftCard card1 = savedCard( 10 );
        GiftCard card2 = savedCard( 10 );

        service.deleteByCardIdStartingWith( EntityDrawer.GiftCardPrefix );
        assertThrows( RuntimeException.class, () -> service.getById( card1.getId() ) );
        assertThrows( RuntimeException.class, () -> service.getById( card2.getId() ) );
    }

    @Test public void aSimpleCard() {
        assertEquals( 10, getBalance(savedCard(10).getCardId()) );
    }

    @Test public void cannotChargeUnownedCards() {
        GiftCard aCard = savedCard(10);
        assertThrows( RuntimeException.class, () -> service.charge( aCard.getCardId() ,2, "Un cargo" ) );
        assertEquals( 10, getBalance(aCard.getCardId()) );
        assertTrue( charges(aCard.getCardId()).isEmpty() );
    }

    @Test public void chargeACard() {
        GiftCard aCard = savedCard(10);
        service.redeem( aCard.getCardId(), "Bob" );
        service.charge( aCard.getCardId(), 2, "Un cargo" );
        assertEquals( 8, getBalance(aCard.getCardId()) );
        assertEquals( "Un cargo", charges(aCard.getCardId()).getLast());
    }

    @Test public void cannotOverrunACard() {
        GiftCard aCard = savedCard(10);
        assertThrows( RuntimeException.class, () -> service.charge( aCard.getCardId(),11, "Un cargo" ) );
        assertEquals( 10, getBalance(aCard.getCardId()) );
    }

    private int getBalance(String cardId) {
        return service.findByCardId(cardId).getBalance();
    }

    private List<String> charges(String cardId) {
        return service.findByCardId(cardId).charges();
    }
}
