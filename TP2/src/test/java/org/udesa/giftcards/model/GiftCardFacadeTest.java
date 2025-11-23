package org.udesa.giftcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class GiftCardFacadeTest {
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

    @AfterAll public void cleanDatabase() {
        userService.deleteByNameStartingWith( "JohnPork" );
        giftCardService.deleteByCardIdStartingWith( "GC" );
        merchantService.deleteByNameStartingWith( "Merchant" );
    }

    private UserVault savedUser() {
        return userService.save( new UserVault( "JohnPork" + nextKey(), "Jpass" ) );
    }
    private GiftCard savedCard( int balance ) { return giftCardService.save( new GiftCard( "GC" + nextKey(), balance ) ); }
    private Merchant savedMerchant() { return merchantService.save( new Merchant( "Merchant" + nextKey() ));}
    private int nextKey() {
        return randomStream.nextInt();
    }

    @Test public void userCanOpenASession() {
        assertNotNull( login( savedUser() ));
    }

    @Test public void unknownUserCannotOpenASession() {

        assertThrows( RuntimeException.class, () -> systemFacade.login( "Stuart", "StuPass" ) );
    }

    @Test public void userCannotUseAnInvalidToken() {
        assertThrows( RuntimeException.class, () -> systemFacade.redeem( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> systemFacade.balance( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> systemFacade.details( UUID.randomUUID(), "GC1" ) );
    }

    @Test public void userCannotCheckOnAlienCard() {
        UUID token = login( savedUser() );
        assertThrows( RuntimeException.class, () -> systemFacade.balance( token, savedCard( 10 ).getCardId() ) ); //Lo cambie porque GC1 nunca esta en nuestro db, entonces el check no es lo q dice el test.
    }

    @Test public void userCanRedeemACard() {
        GiftCard card = savedCard(10);
        UUID token = newUserRedeemsCard(card);
        assertEquals(10, systemFacade.balance(token, card.getCardId()));
    }

    @Test public void userCanRedeemASecondCard() {
        GiftCard card1 = savedCard( 10 );
        GiftCard card2 = savedCard( 5 );
        UUID token = newUserRedeemsCard(card1);
        systemFacade.redeem( token, card2.getCardId() );

        assertEquals( 10, systemFacade.balance( token, card1.getCardId() ) );
        assertEquals( 5, systemFacade.balance( token, card2.getCardId() ) );
    }

    @Test public void userCannotRedeemRedeemedCard() {
        GiftCard card1 = savedCard( 10 );
        UUID token = newUserRedeemsCard(card1);
        assertThrows(RuntimeException.class , () -> systemFacade.redeem( token, card1.getCardId() ));
    }

    @Test public void multipleUsersCanRedeemACard() {
        GiftCard card1 = savedCard(10);
        UUID token1 = newUserRedeemsCard(card1);
        assertEquals(10, systemFacade.balance(token1, card1.getCardId()));
        GiftCard card2 = savedCard(5);
        UUID token2 = newUserRedeemsCard(card2);
        assertEquals(5, systemFacade.balance(token2, card2.getCardId()));
    }

    @Test public void unknownMerchantCantCharge() {
        assertThrows( RuntimeException.class, () -> systemFacade.charge( "Mx", savedCard( 10 ).getCardId(), 2, "UnCargo" ) );
    }

    @Test public void merchantCantChargeUnredeemedCard() {
        assertThrows( RuntimeException.class, () ->
                systemFacade.charge( savedMerchant().getName(), savedCard( 10 ).getCardId(), 2, "UnCargo" ) );
    }

    @Test public void merchantCanChargeARedeemedCard() {
        GiftCard card = savedCard( 10 );
        UUID token = newUserRedeemsCard(card);
        systemFacade.charge( savedMerchant().getName(), card.getCardId(), 2, "UnCargo" );

        assertEquals( 8, systemFacade.balance( token, card.getCardId() ) );
    }

    @Test public void merchantCannotOverchargeACard() {
        GiftCard card = savedCard( 10 );
        newUserRedeemsCard(card);
        assertThrows( RuntimeException.class, () -> systemFacade.charge( savedMerchant().getName(), card.getCardId(), 11, "UnCargo" ) );
    }

    @Test public void userCanCheckHisEmptyCharges() {
        GiftCard card = savedCard( 10 );
        UUID token = newUserRedeemsCard(card);

        assertTrue( systemFacade.details( token, card.getCardId() ).isEmpty() );
    }

    @Test public void userCanCheckHisCharges() {
        GiftCard card = savedCard( 10 );
        UUID token = newUserRedeemsCard(card);
        systemFacade.charge( savedMerchant().getName(), card.getCardId(), 2, "UnCargo" );

        assertEquals( "UnCargo", systemFacade.details( token, card.getCardId() ).getLast());
    }

    @Test public void userCannotCheckOthersCharges() {
        GiftCard card = savedCard( 10 );
        systemFacade.redeem( login( savedUser() ), card.getCardId() );

        UUID token = login( savedUser() );

        assertThrows( RuntimeException.class, () -> systemFacade.details( token, card.getCardId() ) );
    }

    @Test public void tokenExpires() {
        GiftCard card = savedCard( 10 );
        when (clock.now()).thenReturn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(16));
        UUID token = login( savedUser() );

        assertThrows( RuntimeException.class, () -> systemFacade.redeem( token, card.getCardId() ) );
    }

    private UUID login( UserVault user ){
        return systemFacade.login( user.getName(), user.getPassword() );
    }

    private UUID newUserRedeemsCard(GiftCard card) {
        UUID token = login( savedUser() );
        systemFacade.redeem( token, card.getCardId() );
        return token;
    }

}
