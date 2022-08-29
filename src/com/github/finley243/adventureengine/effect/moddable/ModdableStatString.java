package com.github.finley243.adventureengine.effect.moddable;

import java.util.ArrayDeque;
import java.util.Deque;

public class ModdableStatString {

    private final Moddable target;
    private final Deque<String> stringStack;

    public ModdableStatString(Moddable target) {
        this.target = target;
        this.stringStack = new ArrayDeque<>();
    }

    public String value(String base) {
        if (stringStack.isEmpty()) {
            return base;
        } else {
            return stringStack.peek();
        }
    }

    public <E extends Enum<E>> E valueEnum(E base, Class<E> enumType) {
        if (stringStack.isEmpty()) {
            return base;
        } else {
            try {
                return Enum.valueOf(enumType, stringStack.peek().toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public void addMod(String value) {
        stringStack.push(value);
        target.onStatChange();
    }

    public void removeMod(String value) {
        stringStack.remove(value);
        target.onStatChange();
    }



}
