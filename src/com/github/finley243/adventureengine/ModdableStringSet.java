package com.github.finley243.adventureengine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModdableStringSet {

    private final Map<String, Integer> additional;
    private final Map<String, Integer> cancellation;

    public ModdableStringSet() {
        additional = new HashMap<>();
        cancellation = new HashMap<>();
    }

    public Set<String> value(Set<String> base) {
        Set<String> outputSet = new HashSet<>(base);
        outputSet.addAll(additional.keySet());
        outputSet.removeAll(cancellation.keySet());
        return outputSet;
    }

    public void addAdditional(Set<String> values) {
        for(String value : values) {
            if(!additional.containsKey(value)) {
                additional.put(value, 1);
            } else {
                int count = additional.get(value);
                additional.put(value, count + 1);
            }
        }
    }

    public void removeAdditional(Set<String> values) {
        for(String value : values) {
            if(additional.containsKey(value)) {
                int count = additional.get(value);
                if(count == 1) {
                    additional.remove(value);
                } else {
                    additional.put(value, count - 1);
                }
            }
        }
    }

    public void addCancellation(Set<String> values) {
        for(String value : values) {
            if(!cancellation.containsKey(value)) {
                cancellation.put(value, 1);
            } else {
                int count = cancellation.get(value);
                cancellation.put(value, count + 1);
            }
        }
    }

    public void removeCancellation(Set<String> values) {
        for(String value : values) {
            if(cancellation.containsKey(value)) {
                int count = cancellation.get(value);
                if(count == 1) {
                    cancellation.remove(value);
                } else {
                    cancellation.put(value, count - 1);
                }
            }
        }
    }

}
