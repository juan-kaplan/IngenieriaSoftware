package org.udesa.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.Instant;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GiftCardServiceTest extends ModelServiceTest<GiftCard, GiftCardService> {

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

    @Test public void aSimpleCard() {
        assertEquals( 10, service.getBalance(savedCard(10).getCardId()) );
    }

    @Test public void cannotChargeUnownedCards() {
        GiftCard aCard = savedCard(10);
        assertThrows( RuntimeException.class, () -> service.charge( aCard.getCardId() ,2, "Un cargo" ) );
        assertEquals( 10, service.getBalance(aCard.getCardId()) );
        assertTrue( service.charges(aCard.getCardId()).isEmpty() );
    }

    @Test public void chargeACard() {
        GiftCard aCard = savedCard(10);
        service.redeem( aCard.getCardId(), "Bob" );
        service.charge( aCard.getCardId(), 2, "Un cargo" );
        assertEquals( 8, service.getBalance(aCard.getCardId()) );
        assertEquals( "Un cargo", service.charges(aCard.getCardId()).getLast());
    }

    @Test public void cannotOverrunACard() {
        GiftCard aCard = savedCard(10);
        assertThrows( RuntimeException.class, () -> service.charge( aCard.getCardId(),11, "Un cargo" ) );
        assertEquals( 10, service.getBalance(aCard.getCardId()) );
    }


}
