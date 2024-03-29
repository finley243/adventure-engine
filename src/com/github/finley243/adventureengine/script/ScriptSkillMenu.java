package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSkillMenu extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        Expression points = context.getLocalVariables().get("points").getExpression();
        if (points.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, "Points parameter is not an integer");
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, "Actor parameter is not a stat holder");
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, "Actor parameter is not an actor");
        int pointsValue = points.getValueInteger();
        context.game().menuManager().skillMenu(context.game(), actor, pointsValue);
        return new ScriptReturnData(null, null, null);
    }

}
