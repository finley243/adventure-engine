package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatController {

    protected final Game game;

    private final String statParameters;
    private final boolean immutable;

    // Overrides replace a template value
    private final Map<String, Boolean> booleanMap;
    private final Map<String, Integer> integerMap;
    private final Map<String, Float> floatMap;
    private final Map<String, String> stringMap;
    private final Map<String, Set<String>> stringSetMap;

    protected final Context defaultContext;

    private StatController parent;

    public StatController(Game game, String statParameters, Context defaultContext, boolean immutable) {
        this.game = game;
        this.statParameters = statParameters;
        this.defaultContext = defaultContext;
        this.immutable = immutable;
        this.booleanMap = new HashMap<>();
        this.integerMap = new HashMap<>();
        this.floatMap = new HashMap<>();
        this.stringMap = new HashMap<>();
        this.stringSetMap = new HashMap<>();
    }

    public StatController(Game game, String statParameters, Context defaultContext, boolean immutable, Map<String, Boolean> booleanMap, Map<String, Integer> integerMap, Map<String, Float> floatMap, Map<String, String> stringMap, Map<String, Set<String>> stringSetMap) {
        this.game = game;
        this.statParameters = statParameters;
        this.defaultContext = defaultContext;
        this.immutable = immutable;
        this.booleanMap = booleanMap;
        this.integerMap = integerMap;
        this.floatMap = floatMap;
        this.stringMap = stringMap;
        this.stringSetMap = stringSetMap;
    }

    public void setParent(StatController parent) {
        this.parent = parent;
    }

    /**
     * Sets a stat with the given name to the given value
     * @param name the name of the stat
     * @param value an Expression representing the new value for the stat
     * @param context the Context for evaluating the value expression
     * @return true if the stat is set successfully, false otherwise
     */
    public boolean setValue(String name, Expression value, Context context) {
        if (immutable || !getStatParameters().hasStat(name) || value == null || getStatParameters().getParameter(name).dataType() != value.getDataType()) return false;
        if (context == null) context = defaultContext;
        switch (getStatParameters().getParameter(name).dataType()) {
            case BOOLEAN -> booleanMap.put(name, value.getValueBoolean(context));
            case INTEGER -> integerMap.put(name, getBoundedInteger(getStatParameters().getParameter(name), value.getValueInteger(context), context));
            case FLOAT -> floatMap.put(name, getBoundedFloat(getStatParameters().getParameter(name), value.getValueFloat(context), context));
            case STRING -> stringMap.put(name, value.getValueString(context));
            case STRING_SET -> stringSetMap.put(name, value.getValueStringSet(context));
        }
        return true;
    }

    public Expression getValue(String name, Context context) {
        if (!getStatParameters().hasStat(name)) return null;
        return switch (getStatParameters().getParameter(name).dataType()) {
            case BOOLEAN -> booleanMap.containsKey(name) || parent == null ? Expression.constant(booleanMap.get(name)) : parent.getValue(name, context);
            case INTEGER -> integerMap.containsKey(name) || parent == null ? Expression.constant(integerMap.get(name)) : parent.getValue(name, context);
            case FLOAT -> floatMap.containsKey(name) || parent == null ? Expression.constant(floatMap.get(name)) : parent.getValue(name, context);
            case STRING -> stringMap.containsKey(name) || parent == null ? Expression.constant(stringMap.get(name)) : parent.getValue(name, context);
            case STRING_SET -> stringSetMap.containsKey(name) || parent == null ? Expression.constant(stringSetMap.get(name)) : parent.getValue(name, context);
            case null, default -> null;
        };
    }

    protected StatParameters getStatParameters() {
        return game.data().getStatParameters(statParameters);
    }

    private int getBoundedInteger(StatParameters.StatData data, int value, Context context) {
        if (data.minStat() != null) {
            value = Math.max(getValue(data.minStat(), context).getValueInteger(context), value);
        } else if (data.minInt() != null) {
            value = Math.max(data.minInt(), value);
        }
        if (data.maxStat() != null) {
            value = Math.min(getValue(data.maxStat(), context).getValueInteger(context), value);
        } else if (data.maxInt() != null) {
            value = Math.min(data.maxInt(), value);
        }
        return value;
    }

    private float getBoundedFloat(StatParameters.StatData data, float value, Context context) {
        if (data.minStat() != null) {
            value = Math.max(getValue(data.minStat(), context).getValueFloat(context), value);
        } else if (data.minFloat() != null) {
            value = Math.max(data.minFloat(), value);
        }
        if (data.maxStat() != null) {
            value = Math.min(getValue(data.maxStat(), context).getValueFloat(context), value);
        } else if (data.maxFloat() != null) {
            value = Math.min(data.maxFloat(), value);
        }
        return value;
    }

}
