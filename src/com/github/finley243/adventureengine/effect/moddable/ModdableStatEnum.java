package com.github.finley243.adventureengine.effect.moddable;

import java.util.EnumMap;

public class ModdableStatEnum<E extends Enum<E>> {

    private final Moddable target;
    private final EnumMap<E, Integer> valueCounts;

    public ModdableStatEnum(Moddable target, Class<E> type) {
        this.target = target;
        this.valueCounts = new EnumMap<E, Integer>(type);
    }

    public E value(E base) {
        // TODO - Find way to handle priority
        return base;
    }

    public void addMod(E value) {
        if (!valueCounts.containsKey(value)) {
            valueCounts.put(value, 1);
        } else {
            valueCounts.put(value, valueCounts.get(value) + 1);
        }
        target.onStatChange();
    }

    public void removeMod(E value) {
        valueCounts.put(value, valueCounts.get(value) - 1);
        target.onStatChange();
    }

}
