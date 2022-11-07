package com.github.finley243.adventureengine.stat;

import java.util.ArrayDeque;
import java.util.Deque;

public class StatString {

    private final StatHolder target;
    private final Deque<String> stringStack;

    public StatString(StatHolder target) {
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

    public <E extends Enum<E>> String valueFromEnum(E base) {
        return value(base.toString().toLowerCase());
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
