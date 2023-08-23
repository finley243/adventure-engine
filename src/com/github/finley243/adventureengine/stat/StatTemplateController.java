package com.github.finley243.adventureengine.stat;

import java.util.Map;
import java.util.Set;

public class StatTemplateController {

    private final Map<String, Boolean> booleanMap;
    private final Map<String, Integer> integerMap;
    private final Map<String, Float> floatMap;
    private final Map<String, String> stringMap;
    private final Map<String, Set<String>> stringSetMap;

    private StatTemplateController parent;

    public StatTemplateController(Map<String, Boolean> booleanMap, Map<String, Integer> integerMap, Map<String, Float> floatMap, Map<String, String> stringMap, Map<String, Set<String>> stringSetMap) {
        this.booleanMap = booleanMap;
        this.integerMap = integerMap;
        this.floatMap = floatMap;
        this.stringMap = stringMap;
        this.stringSetMap = stringSetMap;
    }

    public void setParent(StatTemplateController parent) {
        this.parent = parent;
    }

    public Boolean getBoolean(String name) {
        if (parent == null) {
            return booleanMap.get(name);
        }
        return booleanMap.getOrDefault(name, parent.getBoolean(name));
    }

    public Integer getInteger(String name) {
        if (parent == null) {
            return integerMap.get(name);
        }
        return integerMap.getOrDefault(name, parent.getInteger(name));
    }

    public Float getFloat(String name) {
        if (parent == null) {
            return floatMap.get(name);
        }
        return floatMap.getOrDefault(name, parent.getFloat(name));
    }

    public String getString(String name) {
        if (parent == null) {
            return stringMap.get(name);
        }
        return stringMap.getOrDefault(name, parent.getString(name));
    }

    public Set<String> getStringSet(String name) {
        if (parent == null) {
            return stringSetMap.get(name);
        }
        return stringSetMap.getOrDefault(name, parent.getStringSet(name));
    }

}
