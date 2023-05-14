package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.List;
import java.util.Map;

public class ScriptScene extends Script {

    private final StatHolderReference actor;
    private final List<String> scenes;

    public ScriptScene(Condition condition, Map<String, Expression> localParameters, StatHolderReference actor, List<String> scenes) {
        super(condition, localParameters);
        this.actor = actor;
        this.scenes = scenes;
    }

    @Override
    public void executeSuccess(Context context) {
        if (!(actor.getHolder(context) instanceof Actor actorCast)) {
            return;
        }
        SceneManager.triggerFromIDs(context.game(), actorCast, actorCast, scenes);
    }

}
