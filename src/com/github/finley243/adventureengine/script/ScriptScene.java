package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
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

    public ScriptScene(Condition condition, StatHolderReference actor, Expression scenes) {
        super(condition);
        if (scenes == null) throw new IllegalArgumentException("ScriptScene scenes is null");
        this.actor = actor;
        this.scenes = scenes;
    }

    @Override
    public void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        if (scenes.getDataType(context) != Expression.DataType.STRING && scenes.getDataType(context) != Expression.DataType.STRING_SET) throw new IllegalArgumentException("ScriptScene scenes is not a string or string set");
        if (!(actor.getHolder(context) instanceof Actor actorCast)) {
            return;
        }
        context.game().eventQueue().addToFront(new ScriptResumeEvent(returnTarget, new ScriptReturn(null, false, false, null)));
        if (scenes.getDataType(context) == Expression.DataType.STRING) {
            context.game().eventQueue().addToFront(new SceneEvent(context.game().data().getScene(scenes.getValueString(context)), null, new Context(context, actorCast, actorCast)));
        } else {
            Set<Scene> sceneValues = new HashSet<>();
            for (String sceneID : scenes.getValueStringSet(context)) {
                sceneValues.add(context.game().data().getScene(sceneID));
            }
            Scene selectedScene = selectScene(context, sceneValues);
            context.game().eventQueue().addToFront(new SceneEvent(selectedScene, null, new Context(context, actorCast, actorCast)));
        }
        context.game().eventQueue().startExecution();
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
