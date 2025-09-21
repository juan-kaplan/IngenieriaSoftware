package org.udesa.tp1.model.repos;

import org.udesa.tp1.model.Repository;
import org.udesa.tp1.model.User;

public class UserRepositoryTest extends RepositoryTest<User> {

    @Override
    protected Repository<User> itemRepositoryWithItemWithKnownId() {
        return new Repository<User>().saveItem(new User(knownId(), password()));
    }

    @Override
    protected Repository<User> emptyItemRepository() {
        return new Repository<>();
    }

    @Override
    protected User newItemWithId(String id) {
        return new User(id, password());
    }

    @Override
    protected String knownId() {
        return "John";
    }

    @Override
    protected String unknownId() {
        return "Sarf";
    }

    protected String password() {
        return "Jpass";
    }
}
