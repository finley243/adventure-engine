package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptScene extends Script {

    public ScriptScene(int line) {
        super(line);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression scene = context.getLocalVariables().get("scene").getExpression();
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        if (scene.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, null, new ScriptErrorData("Scene parameter is not a string", getLine()));
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not a stat holder", getLine()));
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, new ScriptErrorData("Actor parameter is not an actor", getLine()));
        // TODO - Update to non-event scene system
        context.game().menuManager().sceneMenu(context.game(), context.game().data().getScene(scene.getValueString()), null, new Context(context, actor, actor));
        return new ScriptReturnData(null, null, null);
    }

}
