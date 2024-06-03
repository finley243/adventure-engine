package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptAttributeMenu extends Script {

    public ScriptAttributeMenu(int line) {
        super(line);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression points = context.getLocalVariables().get("points").getExpression();
        if (points.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Points parameter is not an integer", getLine()));
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getLine()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getLine()));
        int pointsValue = points.getValueInteger();
        context.game().menuManager().attributeMenu(context.game(), actor, pointsValue);
        return new ScriptReturnData(null, null, null);
    }

}
