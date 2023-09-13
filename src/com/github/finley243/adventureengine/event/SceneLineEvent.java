package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneLine;

import java.util.ArrayList;
import java.util.List;

public class SceneLineEvent implements QueuedEvent {

    private final Scene scene;
    private final SceneLine line;
    private final String lastSceneID;
    private final Context context;
    // If true, ignore the result of the "shouldShow" function (for cases where the condition has already been checked)
    private final boolean forceShow;

    public SceneLineEvent(Scene scene, SceneLine line, String lastSceneID, Context context, boolean forceShow) {
        this.scene = scene;
        this.line = line;
        this.lastSceneID = lastSceneID;
        this.context = context;
        this.forceShow = forceShow;
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    public void execute(Game game) {
        if (forceShow || line.shouldShow(context, lastSceneID)) {
            line.setTriggered();
            if (line.shouldExit()) {
                game.eventQueue().removeQueuedScene(scene);
            } else if (line.hasRedirect()) {
                game.eventQueue().removeQueuedScene(scene);
                game.eventQueue().addToFront(new SceneEvent(game.data().getScene(line.getRedirectID()), scene.getID(), context));
            }
            List<QueuedEvent> eventsToAdd = new ArrayList<>();
            if (line.getText() != null) {
                game.eventBus().post(new RenderTextEvent(line.getText()));
            }
            if (line.getScriptPre() != null) {
                eventsToAdd.add(new ScriptEvent(line.getScriptPre(), context));
            }
            if (line.getSubLines() != null) {
                switch (line.getType()) {
                    case SEQUENTIAL -> {
                        for (SceneLine subLine : line.getSubLines()) {
                            eventsToAdd.add(new SceneLineEvent(scene, subLine, lastSceneID, context, false));
                        }
                    }
                    case SELECTOR -> {
                        for (SceneLine subLine : line.getSubLines()) {
                            if (subLine.shouldShow(context, lastSceneID)) {
                                eventsToAdd.add(new SceneLineEvent(scene, subLine, lastSceneID, context, true));
                                break;
                            }
                        }
                    }
                    case RANDOM -> {
                        List<SceneLine> validLines = new ArrayList<>(line.getSubLines().size());
                        for (SceneLine subLine : line.getSubLines()) {
                            if (subLine.shouldShow(context, lastSceneID)) {
                                validLines.add(subLine);
                            }
                        }
                        SceneLine selectedLine = MathUtils.selectRandomFromList(validLines);
                        eventsToAdd.add(new SceneLineEvent(scene, selectedLine, lastSceneID, context, true));
                    }
                }
            }
            if (line.getScriptPost() != null) {
                eventsToAdd.add(new ScriptEvent(line.getScriptPost(), context));
            }
            game.eventQueue().addAllToFront(eventsToAdd);
        }
    }

    @Override
    public boolean continueAfterExecution() {
        return true;
    }

}
