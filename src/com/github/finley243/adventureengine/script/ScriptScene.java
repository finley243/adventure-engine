package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScriptScene extends Script {

    private final StatHolderReference actor;
    private final Expression scenes;

    public ScriptScene(Condition condition, Map<String, Expression> localParameters, StatHolderReference actor, Expression scenes) {
        super(condition, localParameters);
        if (scenes == null) throw new IllegalArgumentException("ScriptScene scenes is null");
        if (scenes.getDataType() != Expression.DataType.STRING && scenes.getDataType() != Expression.DataType.STRING_SET) throw new IllegalArgumentException("ScriptScene scenes is not a string or string set");
        this.actor = actor;
        this.scenes = scenes;
    }

    @Override
    public void executeSuccess(Context context) {
        if (!(actor.getHolder(context) instanceof Actor actorCast)) {
            return;
        }
        if (scenes.getDataType() == Expression.DataType.STRING) {
            SceneManager.triggerFromID(new Context(context, actorCast, actorCast), scenes.getValueString(context));
        } else {
            SceneManager.triggerFromIDs(new Context(context, actorCast, actorCast), scenes.getValueStringSet(context));
        }
    }

}
