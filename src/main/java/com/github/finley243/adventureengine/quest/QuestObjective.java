package com.github.finley243.adventureengine.quest;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;

public class QuestObjective extends GameInstanced implements ScriptValueHolder {

    public enum State {
        OPEN, COMPLETED, FAILED
    }

    private final QuestManager questManager;
    private final ScriptRuntime scriptRuntime;

    private final Quest parentQuest;
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

    public QuestObjective(QuestManager questManager, ScriptRuntime scriptRuntime, String ID, Quest parentQuest, String name, String description, Condition completionCondition, Condition failureCondition, Script activateScript, Script deactivateScript, Script completionScript, Script failureScript) {
        super(ID);
        this.questManager = questManager;
        this.scriptRuntime = scriptRuntime;
        this.parentQuest = parentQuest;
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

    public Quest getParentQuest() {
        return parentQuest;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update() {
        if (isActive && state == State.OPEN) {
            if (completionCondition != null && completionCondition.isMet(scriptRuntime, Context.builder().build())) {
                setCompleted(true);
            } else if (failureCondition != null && failureCondition.isMet(scriptRuntime, Context.builder().build())) {
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
                questManager.addActiveObjective(this);
                if (activateScript != null) {
                    activateScript.run(scriptRuntime, Context.builder().build());
                }
            } else {
                questManager.removeActiveObjective(this);
                if (deactivateScript != null) {
                    deactivateScript.run(scriptRuntime, Context.builder().build());
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
                completionScript.run(scriptRuntime, Context.builder().build());
            } else if (state == State.FAILED && failureScript != null) {
                failureScript.run(scriptRuntime, Context.builder().build());
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
    public Expression getScriptValue(String name, Context context) {
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
    public boolean setScriptValue(String name, Expression value, Context context) {
        switch (name) {
            case "isActive" -> setActive(value.getValueBoolean());
            case "state" -> state = State.valueOf(value.getValueString());
            case "isCompleted" -> setCompleted(value.getValueBoolean());
            case "isFailed" -> setFailed(value.getValueBoolean());
        }
        return false;
    }

    @Override
    public ScriptValueHolder getSubHolder(String name, String ID) {
        return null;
    }

}
