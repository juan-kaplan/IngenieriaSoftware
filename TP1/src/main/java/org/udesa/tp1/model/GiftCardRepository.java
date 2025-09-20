package org.udesa.tp1.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GiftCardRepository {
    private final Map<String, GiftCard> cardsIndexedById;
    public static String CardNotInRepositoryError = "Gift Card is not in the repository";
    public static String CardAlreadyInRepositoryError = "Gift Card Already in Repository";

    public GiftCardRepository() {
        this.cardsIndexedById = new HashMap<>();;
    }

    public GiftCardRepository saveCard(GiftCard card) {
        Objects.requireNonNull(card);
        if (cardsIndexedById.containsKey(card.giftCardId())){
            throw new IllegalArgumentException(CardAlreadyInRepositoryError);
        }
        cardsIndexedById.put(card.giftCardId(), card);
        return this;
    }

    public GiftCard findById(String giftCardId) {
        if (!existsById(giftCardId)) {
            throw new IllegalArgumentException(CardNotInRepositoryError);
        }
        return cardsIndexedById.get(giftCardId);
    }


    public GiftCardRepository removeCard(String giftCardId) {
        if  (!existsById(giftCardId)) {
            throw new IllegalArgumentException(CardNotInRepositoryError);
        }
        cardsIndexedById.remove(giftCardId);
        return this;
    }

    public boolean existsById(String giftCardId) {
        return cardsIndexedById.containsKey(giftCardId);
    }
}
