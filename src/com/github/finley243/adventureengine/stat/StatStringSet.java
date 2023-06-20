package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.*;

public class StatStringSet extends Stat {

    private final List<StatStringSetMod> mods;

    public StatStringSet(String name, MutableStatHolder target) {
        super(name, target);
        this.mods = new ArrayList<>();
    }

    public Set<String> value(Set<String> base, Context context) {
        Set<String> outputSet = new HashSet<>(base);
        for (StatStringSetMod mod : mods) {
            if (mod.shouldApply(context)) {
                outputSet.addAll(mod.addition);
                outputSet.removeAll(mod.cancellation);
            }
        }
        return outputSet;
    }

    public <T extends Enum<T>> Set<String> valueFromEnum(Set<T> base, Context context) {
        Set<String> enumStrings = new HashSet<>();
        for (T enumValue : base) {
            enumStrings.add(enumValue.toString().toLowerCase());
        }
        return value(enumStrings, context);
    }

    public <T extends Enum<T>> Set<T> valueEnum(Set<T> base, Class<T> enumClass, Context context) {
        Set<T> outputSet = new HashSet<>(base);
        Set<T> additionalEnum = new HashSet<>();
        Set<T> cancellationEnum = new HashSet<>();
        Set<String> additional = new HashSet<>();
        Set<String> cancellation = new HashSet<>();
        for (StatStringSetMod mod : mods) {
            if (mod.shouldApply(context)) {
                additional.addAll(mod.addition);
                cancellation.addAll(mod.cancellation);
            }
        }
        for (String additionalString : additional) {
            additionalEnum.add(enumValue(additionalString, enumClass));
        }
        for (String cancellationString : cancellation) {
            cancellationEnum.add(enumValue(cancellationString, enumClass));
        }
        outputSet.addAll(additionalEnum);
        outputSet.removeAll(cancellationEnum);
        return outputSet;
    }

    public void addMod(StatStringSetMod mod) {
        mods.add(mod);
        getTarget().onStatChange(getName());
    }

    public void removeMod(StatStringSetMod mod) {
        mods.remove(mod);
        getTarget().onStatChange(getName());
    }

    private <T extends Enum<T>> T enumValue(String string, Class<T> enumClass) {
        try {
            return Enum.valueOf(enumClass, string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public record StatStringSetMod(Condition condition, Set<String> addition, Set<String> cancellation) {
        public boolean shouldApply(Context context) {
            return condition == null || condition.isMet(context);
        }
    }

}
