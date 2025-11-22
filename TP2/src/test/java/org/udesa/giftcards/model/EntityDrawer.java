package org.udesa.giftcards.model;

import java.time.Instant;
import java.util.Random;

public class EntityDrawer {

    static private Random rnd = new Random( Instant.now().getEpochSecond() );

    static public GiftCard someCard( int balance) {return new GiftCard("GC" + rnd.nextInt(), balance);}

    static public Merchant someMerchant() {return new Merchant(( "Merchant" + rnd.nextInt()));}

    static public UserVault someUser() {return new UserVault("JohnPork" + rnd.nextInt(), "Jpass");}
}
