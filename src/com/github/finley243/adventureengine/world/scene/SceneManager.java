package com.github.finley243.adventureengine.world.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.world.environment.Area;

public class SceneManager {

	private Area area;
	// If isRandom, a scene is chosen from the list at random. If not, the first scene with conditions met will be chosen.
	private boolean isRandom;
	private float chance;
	private List<String> scenes;
	
	public SceneManager(Area area, boolean isRandom, float chance, List<String> scenes) {
		this.area = area;
		this.isRandom = isRandom;
		this.chance = chance;
		this.scenes = scenes;
	}
	
	public void trigger() {
		if(ThreadLocalRandom.current().nextFloat() < chance) {
			Scene scene;
			if(isRandom) {
				scene = selectSceneRandom();
			} else {
				scene = selectScene();
			}
			if(scene != null) {
				scene.play(area);
			}
		}
	}
	
	private Scene selectScene() {
		for(String sceneID : scenes) {
			Scene scene = Data.getScene(sceneID);
			if(scene.canPlay()) {
				return scene;
			}
		}
		return null;
	}
	
	private Scene selectSceneRandom() {
		List<Scene> validScenes = new ArrayList<Scene>();
		for(String sceneID : scenes) {
			Scene scene = Data.getScene(sceneID);
			if(scene.canPlay()) {
				validScenes.add(scene);
			}
		}
		if(validScenes.isEmpty()) {
			return null;
		}
		return validScenes.get(ThreadLocalRandom.current().nextInt(validScenes.size()));
	}

}
