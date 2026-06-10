package com.github.finley243.adventureengine.gamedata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Registry<T> {

    protected final Map<String, T> entries;

    public Registry(Map<String, T> entries) {
        this.entries = new HashMap<>(entries);
    }

    public T getFromID(String id) {
        return entries.get(id);
    }

    public Collection<T> getAll() {
        return entries.values();
    }

}
