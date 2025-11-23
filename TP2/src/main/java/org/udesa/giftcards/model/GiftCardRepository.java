package org.udesa.giftcards.model;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {

    @EntityGraph(attributePaths = "charges")
    Optional<GiftCard> findByCardId(String cardId);
    void deleteByCardIdStartingWith(String prefix);
}
