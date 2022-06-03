package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SceneManager {

	public static void trigger(Game game, List<Scene> scenes) {
		SceneManager.updateCooldowns(game, scenes);
		Scene scene = selectScene(game, scenes);
		if(scene != null) {
			scene.play(game);
		}
	}

	public static void trigger(Game game, Scene scene) {
		trigger(game, List.of(scene));
	}

	public static void triggerFromIDs(Game game, List<String> sceneIDs) {
		List<Scene> scenes = new ArrayList<>();
		for (String sceneID : sceneIDs) {
			scenes.add(game.data().getScene(sceneID));
		}
		trigger(game, scenes);
	}
	
	private static Scene selectScene(Game game, List<Scene> scenes) {
		List<Scene> validScenes = new ArrayList<>();
		int maxPriority = 0;
		for(Scene scene : scenes) {
			if(scene.canPlay(game)) {
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

	private static void updateCooldowns(Game game, List<Scene> scenes) {
		for (Scene scene : scenes) {
			scene.updateCooldown(game);
		}
	}

}
