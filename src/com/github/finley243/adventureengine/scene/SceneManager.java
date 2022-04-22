package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SceneManager {
	
	public static void trigger(Game game, List<String> scenes) {
		updateCooldowns(game, scenes);
		Scene scene = selectScene(game, scenes);
		if(scene != null) {
			scene.play(game);
		}
	}
	
	private static Scene selectScene(Game game, List<String> scenes) {
		List<Scene> validScenes = new ArrayList<>();
		int maxPriority = 0;
		for(String sceneID : scenes) {
			Scene scene = game.data().getScene(sceneID);
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

	private static void updateCooldowns(Game game, List<String> scenes) {
		for(String scene : scenes) {
			game.data().getScene(scene).updateCooldown(game);
		}
	}

}
