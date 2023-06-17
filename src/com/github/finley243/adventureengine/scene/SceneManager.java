package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {

    public static void trigger(Context context, List<Scene> scenes) {
        Scene scene = selectScene(context, scenes);
        if (scene != null) {
            context.game().menuManager().sceneMenu(context, scene);
        }
    }

    public static void trigger(Context context, Scene scene) {
        if (scene != null) {
            trigger(context, List.of(scene));
        }
    }

    public static void triggerFromIDs(Context context, List<String> sceneIDs) {
        List<Scene> scenes = new ArrayList<>();
        for (String sceneID : sceneIDs) {
            scenes.add(context.game().data().getScene(sceneID));
        }
        trigger(context, scenes);
    }

    private static Scene selectScene(Context context, List<Scene> scenes) {
        List<Scene> validScenes = new ArrayList<>();
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
        return MathUtils.selectRandomFromList(validScenes);
    }

}
