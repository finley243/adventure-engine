package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;
import java.util.Set;

public class StatParameters extends GameInstanced {

    private final Map<String, StatData> parameters;

    public StatParameters(Game game, String ID, Map<String, StatData> parameters) {
        super(game, ID);
        this.parameters = parameters;
    }

    public Set<String> getStats() {
        return parameters.keySet();
    }

    public StatData getParameter(String name) {
        return parameters.get(name);
    }

    public boolean hasStat(String name) {
        return parameters.containsKey(name);
    }

    public record StatData(Expression.DataType dataType, boolean mutable, Boolean booleanPriority, Integer minInt, Integer maxInt, Float minFloat, Float maxFloat, String minStat, String maxStat) {}

}
