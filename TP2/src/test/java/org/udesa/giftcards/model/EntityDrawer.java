package org.udesa.giftcards.model;

import java.time.Instant;
import java.util.Random;

public class EntityDrawer {

    public static final String GiftCardPrefix = "GC";
    public static final String MerchantPrefix = "Merchant";
    public static final String UserPrefix = "JohnPork";
    static private Random rnd = new Random( Instant.now().getEpochSecond() );

    static public GiftCard someCard( int balance) {return new GiftCard(GiftCardPrefix + rnd.nextInt(), balance);}

    static public Merchant someMerchant() {return new Merchant(( MerchantPrefix + rnd.nextInt()));}

    static public UserVault someUser() {return new UserVault(UserPrefix + rnd.nextInt(), "Jpass");}
}
