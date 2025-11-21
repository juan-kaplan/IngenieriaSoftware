package org.udesa.giftcards.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {
    Optional<GiftCard> findByCardId(String cardId);

    @Query("select c from Charge c where c.giftCard.cardId = :cardId")
    List<Charge> findAllChargesByCardId(@Param("cardId") String cardId);
}
