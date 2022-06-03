package com.github.finley243.adventureengine.textgen;

import java.util.LinkedHashMap;

public class NounMapper {

    private final LinkedHashMap<String, Noun> map;

    public NounMapper() {
        this.map = new LinkedHashMap<>();
    }

    public NounMapper put(String key, Noun value) {
        map.put(key, value);
        return this;
    }

    public LinkedHashMap<String, Noun> build() {
        return map;
    }

}
