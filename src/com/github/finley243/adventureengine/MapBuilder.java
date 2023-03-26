package com.github.finley243.adventureengine;

import java.util.LinkedHashMap;

public class MapBuilder<K, V> {

    private final LinkedHashMap<K, V> map;

    public MapBuilder() {
        this.map = new LinkedHashMap<>();
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public LinkedHashMap<K, V> build() {
        return map;
    }

}
