package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SceneEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptScene extends Script {

    /*private final StatHolderReference actor;
    private final Expression scenes;

    public ScriptScene(StatHolderReference actor, Expression scenes) {
        if (scenes == null) throw new IllegalArgumentException("ScriptScene scenes is null");
        this.actor = actor;
        this.scenes = scenes;
    }*/

    @Override
    public ScriptReturnData execute(Context context) {
        Expression scene = context.getLocalVariables().get("scene").getExpression();
        Expression actorExpression = context.getLocalVariables().get("actor").getExpression();
        if (scene.getDataType() != Expression.DataType.STRING) new ScriptReturnData(null, false, false, "Scene parameter is not a string");
        if (actorExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, false, false, "Actor parameter is not a stat holder");
        if (!(actorExpression.getValueStatHolder() instanceof Actor actor)) return new ScriptReturnData(null, false, false, "Actor parameter is not an actor");
        // TODO - Update to non-event scene system
        context.game().eventQueue().addToFront(new SceneEvent(context.game().data().getScene(scene.getValueString()), null, new Context(context, actor, actor)));
        return new ScriptReturnData(null, false, false, null);
    }

}
