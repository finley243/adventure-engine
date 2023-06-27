package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.Map;

public class ScriptBark extends Script {

    private final StatHolderReference actor;
    private final String trigger;

    public ScriptBark(Condition condition, Map<String, Expression> localParameters, StatHolderReference actor, String trigger) {
        super(condition, localParameters);
        this.actor = actor;
        this.trigger = trigger;
    }

    @Override
    protected void executeSuccess(Context context) {
        if (!(actor.getHolder(context) instanceof Actor actorCast)) {
            // TODO - Add error log
            return;
        }
        actorCast.triggerBark(trigger, context.getTarget());
    }

}
