package org.udesa.tp1.model.repos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.udesa.tp1.model.Identifiable;
import org.udesa.tp1.model.Repository;

import static org.junit.jupiter.api.Assertions.*;

public abstract class RepositoryTest<Item extends Identifiable> {

    protected abstract Repository<Item> itemRepositoryWithItemWithKnownId();
    protected abstract Repository<Item> emptyItemRepository();
    protected abstract Item newItemWithId(String id);
    protected abstract String knownId();
    protected abstract String unknownId();


    @Test
    public void testCanFindItemById() {
        assertEquals(knownId(), itemRepositoryWithItemWithKnownId().findById(knownId()).id());
    }

    @Test
    public void testCanSaveItem() {
        Item item = newItemWithId(unknownId());
        assertEquals(item, emptyItemRepository().saveItem(item).findById(unknownId()));
    }

    @Test
    public void testFindUnknownReturnsEmpty() {
        assertThrowsLike(() -> itemRepositoryWithItemWithKnownId().findById(unknownId()), Repository.ItemNotInRepository);
    }

    @Test
    public void testSaveDuplicateThrows() {
        assertThrowsLike(() -> itemRepositoryWithItemWithKnownId().saveItem(newItemWithId(knownId())), Repository.ItemAlreadyInRepository);
    }

    @Test
    public void testRemoveItemFromRepository() {
        assertThrowsLike(() -> itemRepositoryWithItemWithKnownId().removeItem(knownId()).findById(knownId()), Repository.ItemNotInRepository);
    }

    @Test
    public void testCannotRemoveItemThatIsNotPresent() {
        assertThrowsLike(() -> itemRepositoryWithItemWithKnownId().removeItem(unknownId()), Repository.ItemNotInRepository);
    }

    @Test
    public void testVerifyPresenceOfItemInRepository() {
        assertTrue(itemRepositoryWithItemWithKnownId().existsById(knownId()));
    }

    @Test
    public void testVerifyAbsenceOfItemInRepository() {
        assertFalse(itemRepositoryWithItemWithKnownId().existsById(unknownId()));
    }

    // helper identical to your original
    protected void assertThrowsLike(Executable exec, String message) {
        assertEquals(message, assertThrows(Exception.class, exec).getMessage());
    }
}
