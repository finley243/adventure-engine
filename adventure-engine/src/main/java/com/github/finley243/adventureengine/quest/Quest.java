package com.github.finley243.adventureengine.quest;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.ScriptValueHolder;

import java.util.Map;

public class Quest extends GameInstanced implements ScriptValueHolder {

    private final String name;

    private final Map<String, QuestObjective> objectives;

    public Quest(String ID, String name, Map<String, QuestObjective> objectives) {
        super(ID);
        this.name = name;
        this.objectives = objectives;
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression getScriptValue(String name, Context context) {
        if (name.startsWith("objective_")) {
            String objectiveID = name.substring("objective_".length());
            return Expression.valueHolder(objectives.get(objectiveID));
        }
        return switch (name) {
            case "id" -> Expression.string(getID());
            case "name" -> Expression.string(name);
            default -> null;
        };
    }

    @Override
    public boolean setScriptValue(String name, Expression value, Context context) {
        return false;
    }

}
