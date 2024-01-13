package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SceneEvent;
import com.github.finley243.adventureengine.event.ScriptResumeEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.HashSet;
import java.util.Set;

public class ScriptScene extends Script {

    private final StatHolderReference actor;
    private final Expression scenes;

    public ScriptScene(StatHolderReference actor, Expression scenes) {
        if (scenes == null) throw new IllegalArgumentException("ScriptScene scenes is null");
        this.actor = actor;
        this.scenes = scenes;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (scenes.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptScene scenes is not a string");
        if (!(actor.getHolder(context) instanceof Actor actorCast)) {
            return new ScriptReturnData(null, false, false, "StatHolder is not an actor");
        }
        // TODO - Update to non-event scene system
        if (scenes.getDataType(context) == Expression.DataType.STRING) {
            context.game().eventQueue().addToFront(new SceneEvent(context.game().data().getScene(scenes.getValueString(context)), null, new Context(context, actorCast, actorCast)));
        }
        return new ScriptReturnData(null, false, false, null);
    }

}
