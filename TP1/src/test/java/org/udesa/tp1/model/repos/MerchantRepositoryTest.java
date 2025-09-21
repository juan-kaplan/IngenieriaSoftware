package org.udesa.tp1.model.repos;

import org.udesa.tp1.model.Merchant;
import org.udesa.tp1.model.Repository;

public class MerchantRepositoryTest extends RepositoryTest<Merchant> {

    @Override
    protected Repository<Merchant> itemRepositoryWithItemWithKnownId() {
        return new Repository<Merchant>().saveItem(new Merchant(knownId(), merchantName()));
    }

    @Override
    protected Repository<Merchant> emptyItemRepository() {
        return new Repository<>();
    }

    @Override
    protected Merchant newItemWithId(String id) {
        return new Merchant(id, merchantName());
    }

    @Override
    protected String knownId() {
        return "Store1";
    }

    @Override
    protected String unknownId() {
        return "Restaurant1";
    }

    protected String merchantName() {
        return "Generic name";
    }
}
