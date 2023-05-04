package com.github.finley243.adventureengine.stat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatStringSet extends Stat {

    private final Map<String, Integer> additional;
    private final Map<String, Integer> cancellation;

    public StatStringSet(String name, EffectableStatHolder target) {
        super(name, target);
        this.additional = new HashMap<>();
        this.cancellation = new HashMap<>();
    }

    public Set<String> value(Set<String> base) {
        Set<String> outputSet = new HashSet<>(base);
        outputSet.addAll(additional.keySet());
        outputSet.removeAll(cancellation.keySet());
        return outputSet;
    }

    public <T extends Enum<T>> Set<String> valueFromEnum(Set<T> base) {
        Set<String> enumStrings = new HashSet<>();
        for (T enumValue : base) {
            enumStrings.add(enumValue.toString().toLowerCase());
        }
        return value(enumStrings);
    }

    public <T extends Enum<T>> Set<T> valueEnum(Set<T> base, Class<T> enumClass) {
        Set<T> outputSet = new HashSet<>(base);
        Set<T> additionalEnum = new HashSet<>();
        Set<T> cancellationEnum = new HashSet<>();
        for (String additionalString : additional.keySet()) {
            additionalEnum.add(enumValue(additionalString, enumClass));
        }
        for (String cancellationString : cancellation.keySet()) {
            cancellationEnum.add(enumValue(cancellationString, enumClass));
        }
        outputSet.addAll(additionalEnum);
        outputSet.removeAll(cancellationEnum);
        return outputSet;
    }

    public void addAdditional(Set<String> values) {
        for (String value : values) {
            if (!additional.containsKey(value)) {
                additional.put(value, 1);
            } else {
                int count = additional.get(value);
                additional.put(value, count + 1);
            }
        }
        getTarget().onStatChange(getName());
    }

    public void removeAdditional(Set<String> values) {
        for (String value : values) {
            if (additional.containsKey(value)) {
                int count = additional.get(value);
                if (count == 1) {
                    additional.remove(value);
                } else {
                    additional.put(value, count - 1);
                }
            }
        }
        getTarget().onStatChange(getName());
    }

    public void addCancellation(Set<String> values) {
        for (String value : values) {
            if (!cancellation.containsKey(value)) {
                cancellation.put(value, 1);
            } else {
                int count = cancellation.get(value);
                cancellation.put(value, count + 1);
            }
        }
        getTarget().onStatChange(getName());
    }

    public void removeCancellation(Set<String> values) {
        for (String value : values) {
            if (cancellation.containsKey(value)) {
                int count = cancellation.get(value);
                if (count == 1) {
                    cancellation.remove(value);
                } else {
                    cancellation.put(value, count - 1);
                }
            }
        }
        getTarget().onStatChange(getName());
    }

    private <T extends Enum<T>> T enumValue(String string, Class<T> enumClass) {
        try {
            return Enum.valueOf(enumClass, string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
