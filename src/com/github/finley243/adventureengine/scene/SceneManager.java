package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;

import java.util.HashSet;
import java.util.Set;

public class SceneManager {

    public static void trigger(Context context, Set<Scene> scenes) {
        Scene scene = selectScene(context, scenes);
        if (scene != null) {
            context.game().menuManager().sceneMenu(context, scene);
        }
    }

    public static void trigger(Context context, Scene scene) {
        if (scene != null) {
            trigger(context, Set.of(scene));
        }
    }

    public static void triggerFromID(Context context, String sceneID) {
        trigger(context, context.game().data().getScene(sceneID));
    }

    public static void triggerFromIDs(Context context, Set<String> sceneIDs) {
        Set<Scene> scenes = new HashSet<>();
        for (String sceneID : sceneIDs) {
            scenes.add(context.game().data().getScene(sceneID));
        }
        trigger(context, scenes);
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
