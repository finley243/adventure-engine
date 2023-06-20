package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.ArrayDeque;
import java.util.Deque;

public class StatString extends Stat {

    private final Deque<String> stringStack;

    public StatString(String name, MutableStatHolder target) {
        super(name, target);
        this.stringStack = new ArrayDeque<>();
    }

    public String value(String base, Context context) {
        if (stringStack.isEmpty()) {
            return base;
        } else {
            return stringStack.peek();
        }
    }

    public <E extends Enum<E>> E valueEnum(E base, Class<E> enumType, Context context) {
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
        getTarget().onStatChange(getName());
    }

    public void removeMod(String value) {
        stringStack.remove(value);
        getTarget().onStatChange(getName());
    }

    public record StatFloatMod(Condition condition, String value) {
        public boolean shouldApply(Context context) {
            return condition == null || condition.isMet(context);
        }
    }

}
