package org.udesa.giftcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class GifCardFacadeTest {
    // Se espera que el usuario pueda inciar sesion con usuario y password y obtener un token
    //    debe poder usar el token para gestionar la tarjeta.
    //    el token se vence a los 5'

    // las giftcards ya estan definidas en el sistema.
    //    el usuario las reclama, pueden ser varias
    //    puede consultar el saldo y el detalle de gastos de sus tarjetas

    // los merchants pueden hacer cargos en las tarjetas que hayan sido reclamadas.
    //    los cargos se actualizan en el balance de las tarjetas

    public static Random randomStream = new Random( Instant.now().getEpochSecond() );
    @Autowired GiftCardFacade systemFacade;
    @Autowired UserService userService;
    @Autowired GiftCardService giftCardService;
    @Autowired MerchantService merchantService;
    @MockBean Clock clock;

    @BeforeEach public void beforeEach() {
        when( clock.now() ).then( it -> LocalDateTime.now() );
    }

    private UserVault savedUser() {
        return userService.save( new UserVault( "JhonPork" + nextKey(), "Jpass" ) );
    }
    private GiftCard savedCard( int balance ) { return giftCardService.save( new GiftCard( "GC" + nextKey(), balance ) ); }
    private Merchant savedMerchant() { return merchantService.save( new Merchant( "Merchant" + nextKey() ));}
    private int nextKey() {
        return randomStream.nextInt();
    }

    @Test public void userCanOpenASession() {
        UserVault user = savedUser();
        assertNotNull( systemFacade.login( user.getName(), user.getPassword() ) );
    }

    @Test public void unkownUserCannorOpenASession() {
        assertThrows( RuntimeException.class, () -> systemFacade.login( "Stuart", "StuPass" ) );
    }

    @Test public void userCannotUseAnInvalidtoken() {
        assertThrows( RuntimeException.class, () -> systemFacade.redeem( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> systemFacade.balance( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> systemFacade.details( UUID.randomUUID(), "GC1" ) );
    }

    @Test public void userCannotCheckOnAlienCard() {
        UserVault user = savedUser();
        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        assertThrows( RuntimeException.class, () -> systemFacade.balance( token, "GC1" ) );
    }

    @Test public void userCanRedeeemACard() {
        UserVault user = savedUser();
        GiftCard card = savedCard( 10 );
        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        systemFacade.redeem( token, card.getCardId() );
        assertEquals( 10, systemFacade.balance( token, card.getCardId()) );
    }

    @Test public void userCanRedeeemASecondCard() {
        UserVault user = savedUser();
        GiftCard card1 = savedCard( 10 );
        GiftCard card2 = savedCard( 5 );
        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        systemFacade.redeem( token, card1.getCardId() );
        systemFacade.redeem( token, card2.getCardId() );

        assertEquals( 10, systemFacade.balance( token, card1.getCardId() ) );
        assertEquals( 5, systemFacade.balance( token, card2.getCardId() ) );
    }

    @Test public void multipleUsersCanRedeeemACard() {
        UserVault user1 = savedUser();
        UserVault user2 = savedUser();
        GiftCard card1 = savedCard( 10 );
        GiftCard card2 = savedCard( 5 );
        UUID token1 = systemFacade.login( user1.getName(), user1.getPassword() );
        UUID token2 = systemFacade.login( user2.getName(), user2.getPassword() );

        systemFacade.redeem( token1, card1.getCardId() );
        systemFacade.redeem( token2, card2.getCardId() );

        assertEquals( 10, systemFacade.balance( token1, card1.getCardId() ) );
        assertEquals( 5, systemFacade.balance( token2, card2.getCardId() ) );
    }

    @Test public void unknownMerchantCantCharge() {
        GiftCard card = savedCard( 10 );
        assertThrows( RuntimeException.class, () -> systemFacade.charge( "Mx", card.getCardId(), 2, "UnCargo" ) );

    }

    @Test public void merchantCantChargeUnredeemedCard() {
        Merchant merchant = savedMerchant();
        GiftCard card = savedCard( 10 );
        assertThrows( RuntimeException.class, () -> systemFacade.charge( merchant.getName(), card.getCardId(), 2, "UnCargo" ) );
    }

    @Test public void merchantCanChargeARedeemedCard() {
        UserVault user = savedUser();
        Merchant merchant = savedMerchant();
        GiftCard card = savedCard( 10 );
        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        systemFacade.redeem( token, card.getCardId() );
        systemFacade.charge( merchant.getName(), card.getCardId(), 2, "UnCargo" );

        assertEquals( 8, systemFacade.balance( token, card.getCardId() ) );
    }

    @Test public void merchantCannotOverchargeACard() {
        UserVault user = savedUser();
        Merchant merchant = savedMerchant();
        GiftCard card = savedCard( 10 );
        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        systemFacade.redeem( token, card.getCardId() );
        assertThrows( RuntimeException.class, () -> systemFacade.charge( merchant.getName(), card.getCardId(), 11, "UnCargo" ) );
    }

    @Test public void userCanCheckHisEmptyCharges() {
        UserVault user = savedUser();
        GiftCard card = savedCard( 10 );
        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        systemFacade.redeem( token, card.getCardId() );

        assertTrue( systemFacade.details( token, card.getCardId() ).isEmpty() );
    }

    @Test public void userCanCheckHisCharges() {
        UserVault user = savedUser();
        Merchant merchant = savedMerchant();
        GiftCard card = savedCard( 10 );
        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        systemFacade.redeem( token, card.getCardId() );
        systemFacade.charge( merchant.getName(), card.getCardId(), 2, "UnCargo" );

        assertEquals( "UnCargo", systemFacade.details( token, card.getCardId() ).get(
                systemFacade.details(token, card.getCardId()).size() - 1
        ));
    }

    @Test public void userCannotCheckOthersCharges() {
        UserVault user1 = savedUser();
        UserVault user2 = savedUser();
        GiftCard card = savedCard( 10 );
        systemFacade.redeem( systemFacade.login( user1.getName(), user1.getPassword() ), card.getCardId() );

        UUID token = systemFacade.login( user2.getName(), user2.getPassword() );

        assertThrows( RuntimeException.class, () -> systemFacade.details( token, card.getCardId() ) );
    }

    @Test public void tokenExpires() {
        UserVault user = savedUser();
        GiftCard card = savedCard( 10 );
        when (clock.now()).thenReturn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(16));

        UUID token = systemFacade.login( user.getName(), user.getPassword() );

        assertThrows( RuntimeException.class, () -> systemFacade.redeem( token, card.getCardId() ) );
    }
}
