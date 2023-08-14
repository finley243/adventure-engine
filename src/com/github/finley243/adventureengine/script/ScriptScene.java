package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SceneEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.HashSet;
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
            context.game().eventQueue().addToFront(new SceneEvent(context.game().data().getScene(scenes.getValueString(context)), null, new Context(context, actorCast, actorCast)));
        } else {
            Set<Scene> sceneValues = new HashSet<>();
            for (String sceneID : scenes.getValueStringSet(context)) {
                sceneValues.add(context.game().data().getScene(sceneID));
            }
            Scene selectedScene = selectScene(context, sceneValues);
            context.game().eventQueue().addToFront(new SceneEvent(selectedScene, null, new Context(context, actorCast, actorCast)));
        }
        context.game().eventQueue().executeNext();
    }

    private static Scene selectScene(Context context, Set<Scene> scenes) {
        Set<Scene> validScenes = new HashSet<>();
        int maxPriority = 0;
        for (Scene scene : scenes) {
            if (scene.canChoose(context)) {
                if (scene.getPriority() > maxPriority) {
                    validScenes.clear();
                    validScenes.add(scene);
                } else if (scene.getPriority() == maxPriority) {
                    validScenes.add(scene);
                }
            }
        }
        if (validScenes.isEmpty()) {
            return null;
        }
        return MathUtils.selectRandomFromSet(validScenes);
    }

}
