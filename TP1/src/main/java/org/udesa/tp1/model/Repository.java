package org.udesa.tp1.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Repository<Item extends Identifiable> {
    private final Map<String, Item> itemsIndexedById;
    public static String ItemNotInRepository = "Item is not in the repository";
    public static String ItemAlreadyInRepository = "Item is already in Repository";

    public Repository() {
        this.itemsIndexedById = new HashMap<>();;
    }

    public Repository<Item> saveItem(Item item) {
        Objects.requireNonNull(item);
        if (itemsIndexedById.containsKey(item.id())){
            throw new IllegalArgumentException(ItemAlreadyInRepository);
        }
        itemsIndexedById.put(item.id(), item);
        return this;
    }

    public Item findById(String itemId) {
        if (!existsById(itemId)) {
            throw new IllegalArgumentException(ItemNotInRepository);
        }
        return itemsIndexedById.get(itemId);
    }


    public Repository<Item> removeItem(String itemId) {
        if  (!existsById(itemId)) {
            throw new IllegalArgumentException(ItemNotInRepository);
        }
        itemsIndexedById.remove(itemId);
        return this;
    }

    public boolean existsById(String itemId) {
        return itemsIndexedById.containsKey(itemId);
    }
}
