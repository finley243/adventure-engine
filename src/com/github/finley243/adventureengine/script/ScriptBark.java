package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptBark extends Script {

    // List of possible lines to be selected
    private final ActorReference actor;
    private final String trigger;

    public ScriptBark(Condition condition, Map<String, Expression> localParameters, ActorReference actor, String trigger) {
        super(condition, localParameters);
        this.actor = actor;
        this.trigger = trigger;
    }

    @Override
    protected void executeSuccess(Context context) {
        actor.getActor(context).triggerBark(trigger, context.getTarget());
    }

}
