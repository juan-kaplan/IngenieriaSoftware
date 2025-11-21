package org.udesa.giftcards.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GiftCardService extends ModelService< GiftCard, GiftCardRepository >{

    protected GiftCardService(GiftCardRepository repository) {
        super(repository);
    }

    @Override
    protected void updateData(GiftCard existingObject, GiftCard updatedObject) {
        existingObject.setBalance(updatedObject.getBalance());
        existingObject.setOwner(updatedObject.getOwner());
        existingObject.setCharges(updatedObject.getCharges());
    }

    @Transactional( readOnly = true )
    public GiftCard findByCardId( String cardId ){
        return repository.findByCardId( cardId )
                .orElseThrow(() ->new RuntimeException( GiftCardFacade.InvalidGiftCard ));
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

    @Transactional( readOnly = true )
    public List<Charge> findAllChargesByCardId( String cardId ) {
        return repository.findAllChargesByCardId( cardId );
    }

    //@Transactional(readOnly = true)
    //public List<Charge> findAllChargesByCardId(String cardId) {
    //    GiftCard card = findByCardId(cardId); // already throws if not found
    //    return card.getCharges();             // watch out for lazy loading outside tx
    //}
}
