package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SceneManager {

    public static void trigger(Game game, Actor subject, Actor target, List<Scene> scenes) {
        Scene scene = selectScene(subject, target, scenes);
        if(scene != null) {
            game.menuManager().sceneMenu(subject, target, scene);
        }
    }

    public static void trigger(Game game, Actor subject, Actor target, Scene scene) {
        if (scene != null) {
            trigger(game, subject, target, List.of(scene));
        }
    }

    public static void triggerFromIDs(Game game, Actor subject, Actor target, List<String> sceneIDs) {
        List<Scene> scenes = new ArrayList<>();
        for (String sceneID : sceneIDs) {
            scenes.add(game.data().getScene(sceneID));
        }
        trigger(game, subject, target, scenes);
    }

    private static Scene selectScene(Actor subject, Actor target, List<Scene> scenes) {
        List<Scene> validScenes = new ArrayList<>();
        int maxPriority = 0;
        for(Scene scene : scenes) {
            if(scene.canChoose(subject, target)) {
                if (scene.getPriority() > maxPriority) {
                    validScenes.clear();
                    validScenes.add(scene);
                } else if (scene.getPriority() == maxPriority) {
                    validScenes.add(scene);
                }
            }
        }
        if(validScenes.isEmpty()) {
            return null;
        }
        return validScenes.get(ThreadLocalRandom.current().nextInt(validScenes.size()));
    }

}
