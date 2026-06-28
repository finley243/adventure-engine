package com.github.finley243.adventureengine.gamedata;

import java.util.HashMap;
import java.util.Map;

public class PhraseManager {

    private final Map<String, String> phrases;

    public PhraseManager(Map<String, String> phrases) {
        this.phrases = new HashMap<>(phrases);
    }

    public String get(String id) {
        return phrases.get(id);
    }

}
