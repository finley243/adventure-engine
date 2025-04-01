package com.github.finley243.adventureengine.quest;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.StatHolder;

public class QuestObjective extends GameInstanced implements StatHolder {

    public enum State {
        OPEN, COMPLETED, FAILED
    }

    private final String name;
    private final String description;

    private final Condition completionCondition;
    private final Condition failureCondition;

    private final Script activateScript;
    private final Script deactivateScript;

    private final Script completionScript;
    private final Script failureScript;

    private boolean isActive;
    private State state;

    public QuestObjective(Game game, String ID, String name, String description, Condition completionCondition, Condition failureCondition, Script activateScript, Script deactivateScript, Script completionScript, Script failureScript) {
        super(game, ID);
        this.name = name;
        this.description = description;
        this.completionCondition = completionCondition;
        this.failureCondition = failureCondition;
        this.activateScript = activateScript;
        this.deactivateScript = deactivateScript;
        this.completionScript = completionScript;
        this.failureScript = failureScript;
        this.isActive = false;
        this.state = State.OPEN;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update() {
        if (isActive && state == State.OPEN) {
            if (completionCondition != null && completionCondition.isMet(new Context(game(), game().data().getPlayer(), game().data().getPlayer()))) {
                setCompleted(true);
            } else if (failureCondition != null && failureCondition.isMet(new Context(game(), game().data().getPlayer(), game().data().getPlayer()))) {
                setFailed(true);
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        if (isActive != active) {
            if (active) {
                if (activateScript != null) {
                    activateScript.execute(new Context(game(), game().data().getPlayer(), game().data().getPlayer()));
                }
            } else {
                if (deactivateScript != null) {
                    deactivateScript.execute(new Context(game(), game().data().getPlayer(), game().data().getPlayer()));
                }
            }
        }
        isActive = active;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state != state) {
            if (state == State.COMPLETED && completionScript != null) {
                completionScript.execute(new Context(game(), game().data().getPlayer(), game().data().getPlayer()));
            } else if (state == State.FAILED && failureScript != null) {
                failureScript.execute(new Context(game(), game().data().getPlayer(), game().data().getPlayer()));
            }
        }
        this.state = state;
    }

    public boolean isCompleted() {
        return state == State.COMPLETED;
    }

    public boolean isFailed() {
        return state == State.FAILED;
    }

    public void setCompleted(boolean isCompleted) {
        if (isCompleted) {
            setState(State.COMPLETED);
        } else if (state == State.COMPLETED) {
            setState(State.OPEN);
        }
    }

    public void setFailed(boolean isFailed) {
        if (isFailed) {
            setState(State.FAILED);
        } else if (state == State.FAILED) {
            setState(State.OPEN);
        }
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "id" -> Expression.constant(getID());
            case "name" -> Expression.constant(name);
            case "description" -> Expression.constant(description);
            case "isActive" -> Expression.constant(isActive);
            case "state" -> Expression.constant(state.toString());
            case "isCompleted" -> Expression.constant(isCompleted());
            case "isFailed" -> Expression.constant(isFailed());
            default -> null;
        };
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
        switch (name) {
            case "isActive" -> setActive(value.getValueBoolean());
            case "state" -> state = State.valueOf(value.getValueString());
            case "isCompleted" -> setCompleted(value.getValueBoolean());
            case "isFailed" -> setFailed(value.getValueBoolean());
        }
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

}
