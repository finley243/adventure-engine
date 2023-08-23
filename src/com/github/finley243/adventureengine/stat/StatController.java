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

    // Overrides replace a template value
    private final Map<String, Boolean> booleanOverride;
    private final Map<String, Integer> integerOverride;
    private final Map<String, Float> floatOverride;
    private final Map<String, String> stringOverride;
    private final Map<String, Set<String>> stringSetOverride;

    protected final Context defaultContext;

    private StatTemplateController template;

    public StatController(Game game, String statParameters, Context defaultContext) {
        this.game = game;
        this.statParameters = statParameters;
        this.defaultContext = defaultContext;
        this.booleanOverride = new HashMap<>();
        this.integerOverride = new HashMap<>();
        this.floatOverride = new HashMap<>();
        this.stringOverride = new HashMap<>();
        this.stringSetOverride = new HashMap<>();
    }

    public void setTemplate(StatTemplateController template) {
        this.template = template;
    }

    /**
     * Sets a static stat with the given name to the given value
     * @param name the name of the stat
     * @param value an Expression representing the new value for the stat
     * @param context the Context for evaluating the value expression
     * @return true if the stat is set successfully, false otherwise
     */
    public boolean setValue(String name, Expression value, Context context) {
        if (!getStatParameters().hasStat(name) || value == null || getStatParameters().getParameter(name).dataType() != value.getDataType()) return false;
        if (context == null) context = defaultContext;
        switch (getStatParameters().getParameter(name).dataType()) {
            case BOOLEAN -> booleanOverride.put(name, value.getValueBoolean(context));
            case INTEGER -> integerOverride.put(name, getBoundedInteger(getStatParameters().getParameter(name), value.getValueInteger(context), context));
            case FLOAT -> floatOverride.put(name, getBoundedFloat(getStatParameters().getParameter(name), value.getValueFloat(context), context));
            case STRING -> stringOverride.put(name, value.getValueString(context));
            case STRING_SET -> stringSetOverride.put(name, value.getValueStringSet(context));
        }
        return true;
    }

    public Expression getValue(String name, Context context) {
        return switch (getStatParameters().getParameter(name).dataType()) {
            case BOOLEAN -> Expression.constant(booleanOverride.getOrDefault(name, template.getBoolean(name)));
            case INTEGER -> Expression.constant(integerOverride.getOrDefault(name, template.getInteger(name)));
            case FLOAT -> Expression.constant(floatOverride.getOrDefault(name, template.getFloat(name)));
            case STRING -> Expression.constant(stringOverride.getOrDefault(name, template.getString(name)));
            case STRING_SET -> Expression.constant(stringSetOverride.getOrDefault(name, template.getStringSet(name)));
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
