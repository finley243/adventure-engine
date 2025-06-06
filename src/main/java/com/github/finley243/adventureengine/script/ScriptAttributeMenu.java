package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptAttributeMenu extends Script {

    public ScriptAttributeMenu(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression points = context.getLocalVariables().get("points").getExpression();
        if (points.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Points parameter is not an integer", getTraceData()));
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getTraceData()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getTraceData()));
        int pointsValue = points.getValueInteger();
        context.game().menuManager().attributeMenu(context.game(), actor, pointsValue, context.game().data().getAttributeIDs());
        return new ScriptReturnData(null, null, null);
    }

}
