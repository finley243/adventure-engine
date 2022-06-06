package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptRevealObject extends Script {

    private final String objectID;

    public ScriptRevealObject(Condition condition, String objectID) {
        super(condition);
        this.objectID = objectID;
    }

    @Override
    protected void executeSuccess(Actor subject) {
        subject.game().data().getObject(objectID).setHidden(false);
    }
}
