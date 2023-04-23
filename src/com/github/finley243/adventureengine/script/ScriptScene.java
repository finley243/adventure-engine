package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;
import java.util.Map;

public class ScriptScene extends Script {

    private final ActorReference actor;
    private final List<String> scenes;

    public ScriptScene(Condition condition, Map<String, Expression> localParameters, ActorReference actor, List<String> scenes) {
        super(condition, localParameters);
        this.actor = actor;
        this.scenes = scenes;
    }

    @Override
    public void executeSuccess(Context context) {
        SceneManager.triggerFromIDs(context.game(), actor.getActor(context), actor.getActor(context), scenes);
    }

}
