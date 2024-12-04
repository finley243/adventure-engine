package com.github.finley243.adventureengine.quest;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.StatHolder;

public class QuestObjective implements StatHolder {

    public enum State {
        OPEN, COMPLETED, FAILED
    }

    private final String ID;
    private final String name;
    private final String description;

    private final Script completionScript;
    private final Script failureScript;

    private boolean isActive;
    private State state;

    public QuestObjective(String ID, String name, String description, Script completionScript, Script failureScript) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.completionScript = completionScript;
        this.failureScript = failureScript;
        this.isActive = false;
        this.state = State.OPEN;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isCompleted() {
        return state == State.COMPLETED;
    }

    public boolean isFailed() {
        return state == State.FAILED;
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
