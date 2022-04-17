package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.item.template.ItemTemplate;

import java.util.HashMap;
import java.util.Map;

public class ItemState {

    private final Map<String, Integer> stateInt;

    public ItemState(ItemTemplate item) {
        this.stateInt = new HashMap<>(item.getDefaultStateInt());
    }

    public void setStateInt(String var, int value) {
        stateInt.put(var, value);
    }

    public int getStateInt(String var) {
        return stateInt.get(var);
    }

}
