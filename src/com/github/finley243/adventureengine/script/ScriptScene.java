package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptScene extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression scene = context.getLocalVariables().get("scene").getExpression();
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        if (scene.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, null, "Scene parameter is not a string");
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, "Actor parameter is not a stat holder");
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, null, "Actor parameter is not an actor");
        // TODO - Update to non-event scene system
        context.game().menuManager().sceneMenu(context.game(), context.game().data().getScene(scene.getValueString()), null, new Context(context, actor, actor));
        return new ScriptReturnData(null, null, null);
    }

}
