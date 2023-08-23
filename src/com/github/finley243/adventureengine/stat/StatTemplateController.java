package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;
import java.util.Set;

public class StatTemplateController extends StatController {

    private final Map<String, Boolean> booleanMap;
    private final Map<String, Integer> integerMap;
    private final Map<String, Float> floatMap;
    private final Map<String, String> stringMap;
    private final Map<String, Set<String>> stringSetMap;

    private StatTemplateController parent;

    public StatTemplateController(Game game, String statParameters, Map<String, Boolean> booleanMap, Map<String, Integer> integerMap, Map<String, Float> floatMap, Map<String, String> stringMap, Map<String, Set<String>> stringSetMap) {
        super(game, statParameters, null);
        this.booleanMap = booleanMap;
        this.integerMap = integerMap;
        this.floatMap = floatMap;
        this.stringMap = stringMap;
        this.stringSetMap = stringSetMap;
    }

    @Override
    public boolean setValue(String name, Expression value, Context context) {
        return false;
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
