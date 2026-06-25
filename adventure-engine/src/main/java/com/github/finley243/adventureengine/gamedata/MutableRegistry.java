package com.github.finley243.adventureengine.gamedata;

import java.util.Map;

public class MutableRegistry<T> extends Registry<T> {

    public MutableRegistry(Map<String, T> entries) {
        super(entries);
    }

    public void add(String id, T value) {
        if (id.trim().isEmpty()) throw new IllegalArgumentException("ID cannot be blank");
        if (this.entries.containsKey(id)) throw new IllegalArgumentException("Cannot add to registry with existing ID: " + id);
        this.entries.put(id, value);
    }

    public boolean remove(String id) {
        return this.entries.remove(id) != null;
    }

}
