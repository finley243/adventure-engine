package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneLine;

import java.util.ArrayList;
import java.util.List;

public class SceneEvent implements QueuedEvent {

    private final Scene scene;
    private final String lastSceneID;
    private final Context context;

    public SceneEvent(Scene scene, String lastSceneID, Context context) {
        this.scene = scene;
        this.lastSceneID = lastSceneID;
        this.context = context;
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    public void execute(Game game) {
        scene.setTriggered();
        List<QueuedEvent> sceneEvents = new ArrayList<>();
        switch (scene.getType()) {
            case SEQUENTIAL -> {
                for (SceneLine line : scene.getLines()) {
                    sceneEvents.add(new SceneLineEvent(scene, line, lastSceneID, context, false));
                }
            }
            case SELECTOR -> {
                for (SceneLine line : scene.getLines()) {
                    if (line.shouldShow(context, lastSceneID)) {
                        sceneEvents.add(new SceneLineEvent(scene, line, lastSceneID, context, true));
                        break;
                    }
                }
            }
            case RANDOM -> {
                List<SceneLine> validLines = new ArrayList<>();
                for (SceneLine line : scene.getLines()) {
                    if (line.shouldShow(context, lastSceneID)) {
                        validLines.add(line);
                    }
                }
                SceneLine selectedLine = MathUtils.selectRandomFromList(validLines);
                sceneEvents.add(new SceneLineEvent(scene, selectedLine, lastSceneID, context, true));
            }
        }
        if (!scene.getChoices().isEmpty()) {
            sceneEvents.add(new SceneChoiceEvent(scene, scene.getChoices(), context));
        }
        game.eventQueue().addAllToFront(sceneEvents);
        game.eventQueue().executeNext();
    }

}
