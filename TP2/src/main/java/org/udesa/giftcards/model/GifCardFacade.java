package org.udesa.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GifCardFacade {
    public static final String InvalidUser = "InvalidUser";
    public static final String InvalidMerchant = "InvalidMerchant";
    public static final String InvalidToken = "InvalidToken";

    @Autowired private UserService userService;
    private Map<String,GiftCard> cards;
    private MerchantService  merchantService;
    @Autowired private Clock clock;

    private Map<UUID, UserSession> sessions = new HashMap();

    public GifCardFacade( List<GiftCard> cards, List<String> merchants) {
        this.cards = cards.stream().collect( Collectors.toMap( each -> each.id(), each -> each ));
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
        cards.get( cardId ).redeem( findUser( token ) );
    }

    public int balance( UUID token, String cardId ) {
        return ownedCard( token, cardId ).balance();
    }

    public void charge( String merchantName, String cardId, int amount, String description ) {
        merchantService.findByName(merchantName);

        cards.get( cardId ).charge( amount, description ); // Esto en realidad esta mal xq deberia hacer una exception. Pero igual lo vamos a temrinar cambiando por un cardservice.
    }

    public List<String> details( UUID token, String cardId ) {
        return ownedCard( token, cardId ).charges();
    }

    private GiftCard ownedCard( UUID token, String cardId ) {
        GiftCard card = cards.get( cardId );
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
}
