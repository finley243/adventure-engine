package com.github.finley243.adventureengine;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {

    private final Map<K, V> map;

    public MapBuilder() {
        this.map = new HashMap<>();
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putIf(boolean condition, K key, V value) {
        if (condition) {
            map.put(key, value);
        }
        return this;
    }

    public Map<K, V> build() {
        return map;
    }

}
