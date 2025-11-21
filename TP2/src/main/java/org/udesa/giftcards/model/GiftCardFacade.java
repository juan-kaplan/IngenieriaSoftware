package org.udesa.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GiftCardFacade {
    public static final String InvalidUser = "InvalidUser";
    public static final String InvalidMerchant = "InvalidMerchant";
    public static final String InvalidToken = "InvalidToken";
    public static final String InvalidGiftCard = "InvalidGiftCard";

    @Autowired private UserService userService;
    @Autowired private GiftCardService cardService;
    @Autowired private MerchantService  merchantService;
    @Autowired private Clock clock;

    private Map<UUID, UserSession> sessions = new HashMap();

    public GiftCardFacade() {
    }

    public UUID login( String userKey, String pass ) {
        if (!validateLogin( userKey, pass )) {
            throw new RuntimeException( InvalidUser );
        }

        UUID token = UUID.randomUUID();
        sessions.put( token, new UserSession( userKey, clock ) );
        return token;
    }

    public void redeem( UUID token, String cardId ) {
        cardService.redeem( cardId, findUser( token ) );
    }

    public int balance( UUID token, String cardId ) {
        return ownedCard( token, cardId ).getBalance();
    }

    public void charge( String merchantName, String cardId, int amount, String description ) {
        validateMerchant( merchantName );

        cardService.charge( cardId, amount, description );
    }

    @Transactional(readOnly = true)
    public List<String> details( UUID token, String cardId ) {
        return ownedCard( token, cardId ).charges();
    }

    private GiftCard ownedCard( UUID token, String cardId ) {
        GiftCard card = cardService.findByCardId( cardId );
        if ( !card.isOwnedBy( findUser( token ) ) ) throw new RuntimeException( InvalidToken );
        return card;
    }

    private String findUser( UUID token ) {
        return sessions.computeIfAbsent( token, key -> { throw new RuntimeException( InvalidToken ); } )
                       .userAliveAt( clock );
    }

    private boolean validateLogin(String username, String pass) {
        UserVault user = userService.findByName(username);

        if (!user.getPassword().equals(pass))
            throw new RuntimeException( InvalidUser );

        return true;
    }

    private boolean validateMerchant( String merchantName ) {
        if (merchantService.findByName(merchantName) == null)
            throw new RuntimeException( InvalidMerchant );
        return true;
    }
}
