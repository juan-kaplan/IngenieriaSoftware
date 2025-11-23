package org.udesa.giftcards.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GiftCardService extends ModelService< GiftCard, GiftCardRepository >{

    protected GiftCardService(GiftCardRepository repository) {
        super(repository);
    }

    @Transactional( readOnly = true )
    public GiftCard findByCardId( String cardId ){
        return repository.findByCardId( cardId )
                .orElseThrow(() ->new RuntimeException( GiftCardFacade.InvalidGiftCard ));
    }

    @Transactional
    public void deleteByCardIdStartingWith( String prefix ) {
        repository.deleteByCardIdStartingWith( prefix );
    }

    @Transactional
    public void redeem(String cardId, String newOwner) {
        GiftCard card = findByCardId(cardId);
        card.redeem(newOwner);
    }

    @Transactional
    public void charge(String cardId, int amount, String description) {
        GiftCard card = findByCardId(cardId);
        card.charge( amount, description);
    }
}
