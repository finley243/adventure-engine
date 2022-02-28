package com.github.finley243.adventureengine.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;

public class SceneManager {
	
	public static void trigger(List<String> scenes) {
		updateCooldowns(scenes);
		Scene scene = selectScene(scenes);
		if(scene != null) {
			scene.play();
		}
	}
	
	private static Scene selectScene(List<String> scenes) {
		List<Scene> validScenes = new ArrayList<>();
		for(String sceneID : scenes) {
			Scene scene = Data.getScene(sceneID);
			if(scene.canPlay()) {
				if(scene.playImmediately()) {
					return scene;
				}
				validScenes.add(scene);
			}
		}
		if(validScenes.isEmpty()) {
			return null;
		}
		return validScenes.get(ThreadLocalRandom.current().nextInt(validScenes.size()));
	}

	private static void updateCooldowns(List<String> scenes) {
		for(String scene : scenes) {
			Data.getScene(scene).updateCooldown();
		}
	}

}
