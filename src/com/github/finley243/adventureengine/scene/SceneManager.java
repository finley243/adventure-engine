package com.github.finley243.adventureengine.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;

public class SceneManager {
	
	public SceneManager() {
		
	}
	
	public void trigger() {
		Scene scene = selectScene();
		if(scene != null) {
			scene.play();
		}
	}
	
	private Scene selectScene() {
		List<Scene> validScenes = new ArrayList<Scene>();
		for(Scene scene : Data.getScenes()) {
			scene.updateCooldown();
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

}
