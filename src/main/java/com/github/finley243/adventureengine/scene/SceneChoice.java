package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.gamedata.Registry;

public class SceneChoice {

    private String linkedSceneID;
    private Scene linkedScene;
    private final String prompt;

    public SceneChoice(String linkedSceneID, String prompt) {
        this.linkedSceneID = linkedSceneID;
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void resolveLinkedScene(Registry<Scene> sceneRegistry) {
        if (linkedScene != null) throw new GameDataException("Linked scene has already been resolved");
        Scene scene = sceneRegistry.getFromID(linkedSceneID);
        if (scene == null) throw new GameDataException("SceneChoice has invalid linked scene");
        linkedScene = scene;
        linkedSceneID = null;
    }

    public Scene getLinkedScene() {
        if (linkedScene == null) throw new GameDataException("Linked scene has not been resolved");
        return linkedScene;
    }

}
