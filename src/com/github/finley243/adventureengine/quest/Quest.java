package com.github.finley243.adventureengine.quest;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;

import java.util.Map;

public class Quest implements StatHolder {

    private final String ID;
    private final String name;

    private final Map<String, QuestObjective> objectives;

    public Quest(String ID, String name, Map<String, QuestObjective> objectives) {
        this.ID = ID;
        this.name = name;
        this.objectives = objectives;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return null;
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

}
