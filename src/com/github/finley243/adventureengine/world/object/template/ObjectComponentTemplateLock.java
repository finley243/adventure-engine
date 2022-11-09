package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

import java.util.Set;

public class ObjectComponentTemplateLock extends ObjectComponentTemplate {

    private final Set<String> keyItems;
    private final Integer lockpickLevel;
    private final Integer hotwireLevel;

    public ObjectComponentTemplateLock(Game game, String ID, boolean startEnabled, Set<String> keyItems, Integer lockpickLevel, Integer hotwireLevel) {
        super(game, ID, startEnabled);
        this.keyItems = keyItems;
        this.lockpickLevel = lockpickLevel;
        this.hotwireLevel = hotwireLevel;
    }

    public Set<String> getKeyItems() {
        return keyItems;
    }

    public Integer getLockpickLevel() {
        return lockpickLevel;
    }

    public Integer getHotwireLevel() {
        return hotwireLevel;
    }

}
