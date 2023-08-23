package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatController {

    protected final Game game;

    protected final Map<String, StatData> statData;

    // Overrides replace a template value
    private final Map<String, Boolean> booleanOverride;
    private final Map<String, Integer> integerOverride;
    private final Map<String, Float> floatOverride;
    private final Map<String, String> stringOverride;
    private final Map<String, Set<String>> stringSetOverride;

    private StatTemplateController template;

    public StatController(Game game, Map<String, StatData> statData) {
        this.game = game;
        this.statData = statData;
        this.booleanOverride = new HashMap<>();
        this.integerOverride = new HashMap<>();
        this.floatOverride = new HashMap<>();
        this.stringOverride = new HashMap<>();
        this.stringSetOverride = new HashMap<>();
    }

    public Expression getValue(String name, Context context) {
        return switch (statData.get(name).dataType()) {
            case BOOLEAN -> Expression.constant(booleanOverride.getOrDefault(name, template.getBoolean(name)));
            case INTEGER -> Expression.constant(integerOverride.getOrDefault(name, template.getInteger(name)));
            case FLOAT -> Expression.constant(floatOverride.getOrDefault(name, template.getFloat(name)));
            case STRING -> Expression.constant(stringOverride.getOrDefault(name, template.getString(name)));
            case STRING_SET -> Expression.constant(stringSetOverride.getOrDefault(name, template.getStringSet(name)));
            case null, default -> null;
        };
    }

    public record StatData(Expression.DataType dataType, Integer minInt, Integer maxInt, Float minFloat, Float maxFloat) {}

}
