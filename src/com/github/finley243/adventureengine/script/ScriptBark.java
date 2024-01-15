package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptBark extends Script {

    /*private final StatHolderReference actorReference;
    private final String trigger;

    public ScriptBark(/*StatHolderReference actorReference, String trigger) {
        this.actorReference = actorReference;
        this.trigger = trigger;
    }*/

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression trigger = context.getLocalVariables().get("bark").getExpression();
        if (trigger.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, false, false, "Bark parameter is not a string");
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, false, false, "Actor parameter is not a stat holder");
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, false, false, "Actor parameter is not an actor");
        actor.triggerBark(trigger.getValueString(), context);
        return new ScriptReturnData(null, false, false, null);
    }

}
