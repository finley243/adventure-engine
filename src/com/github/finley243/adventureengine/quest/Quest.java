package com.github.finley243.adventureengine.quest;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;

import java.util.Map;

public class Quest extends GameInstanced implements StatHolder {

    private final String name;

    private final Map<String, QuestObjective> objectives;

    public Quest(Game game, String ID, String name, Map<String, QuestObjective> objectives) {
        super(game, ID);
        this.name = name;
        this.objectives = objectives;
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "id" -> Expression.constant(getID());
            case "name" -> Expression.constant(name);
            default -> null;
        };
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        if ("objective".equals(name)) {
            return objectives.get(ID);
        }
        return null;
    }

}
