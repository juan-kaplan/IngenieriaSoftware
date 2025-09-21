package org.udesa.tp1.model.repos;

import org.udesa.tp1.model.GiftCard;
import org.udesa.tp1.model.Repository;

public class GiftCardRepositoryTest extends RepositoryTest<GiftCard> {

    @Override
    protected Repository<GiftCard> itemRepositoryWithItemWithKnownId() {
        return new Repository<GiftCard>().saveItem(new GiftCard(200, knownId()));
    }

    @Override
    protected Repository<GiftCard> emptyItemRepository() {
        return new Repository<>();
    }

    @Override
    protected GiftCard newItemWithId(String id) {
        return new GiftCard(100, id);
    }

    @Override
    protected String knownId() {
        return "CardWith100";
    }

    @Override
    protected String unknownId() {
        return "CardNotInRepository";
    }
}
