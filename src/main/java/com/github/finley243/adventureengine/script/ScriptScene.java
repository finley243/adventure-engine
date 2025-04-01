package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptScene extends Script {

    public ScriptScene(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression scene = context.getLocalVariables().get("scene").getExpression();
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        if (scene.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, null, new ScriptErrorData("Scene parameter is not a string", getTraceData()));
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getTraceData()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getTraceData()));
        // TODO - Update to non-event scene system
        context.game().menuManager().sceneMenu(context.game(), context.game().data().getScene(scene.getValueString()), new Context(context, actor, actor), false);
        return new ScriptReturnData(null, null, null);
    }

}
