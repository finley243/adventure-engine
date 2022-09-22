package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptComponentState extends Script {

    private final String objectID;
    private final String componentID;
    private final boolean state;

    public ScriptComponentState(Condition condition, String objectID, String componentID, boolean state) {
        super(condition);
        this.objectID = objectID;
        this.componentID = componentID;
        this.state = state;
    }

    @Override
    protected void executeSuccess(Actor subject, Actor target) {
        subject.game().data().getObject(objectID).getComponent(componentID).setEnabled(state);
    }

}
