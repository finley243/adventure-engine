package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneLine;

import java.util.ArrayList;
import java.util.List;

public class SceneLineEvent implements QueuedEvent {

    private final SceneLine line;
    private final String lastSceneID;
    private final Context context;

    public SceneLineEvent(SceneLine line, String lastSceneID, Context context) {
        this.line = line;
        this.lastSceneID = lastSceneID;
        this.context = context;
    }

    @Override
    public void execute(Game game) {
        if (line.shouldShow(context, lastSceneID)) {
            List<QueuedEvent> eventsToAdd = new ArrayList<>();
            line.setTriggered();
            if (line.getText() != null) {
                game.eventBus().post(new RenderTextEvent(line.getText()));
            }
            if (line.getScript() != null) {
                eventsToAdd.add(new ScriptEvent(line.getScript(), context));
            }
            if (line.getSubLines() != null) {
                List<SceneLine> lines = selectValidLines(context, line.getType(), line.getSubLines(), lastSceneID);
                switch (line.getType()) {
                    case SEQUENTIAL -> {
                        for (SceneLine subLine : lines) {
                            eventsToAdd.add(new SceneLineEvent(subLine, lastSceneID, context));
                        }
                    }
                    case SELECTOR, RANDOM -> {
                        if (!lines.isEmpty()) {
                            eventsToAdd.add(new SceneLineEvent(lines.get(0), lastSceneID, context));
                        }
                    }
                }
                /*for (SceneLine subLine : lines) {
                    if (line.getType() != Scene.SceneType.SEQUENTIAL || subLine.shouldShow(context, lastSceneID)) {
                        MenuManager.SceneLineResult subLineResult = executeLine(context, subLine, lastSceneID);
                        if (subLineResult.shouldEndLine()) {
                            return subLineResult;
                        }
                    }
                }*/
            }
            game.eventQueue().addAllToFront(eventsToAdd);
        }
        game.eventQueue().executeNext();
    }

    private List<SceneLine> selectValidLines(Context context, Scene.SceneType type, List<SceneLine> lines, String lastSceneID) {
        List<SceneLine> validLines = new ArrayList<>();
        if (type == Scene.SceneType.RANDOM) {
            List<SceneLine> randomSelectionLines = new ArrayList<>();
            for (SceneLine line : lines) {
                if (line.shouldShow(context, lastSceneID)) {
                    randomSelectionLines.add(line);
                }
            }
            validLines.add(MathUtils.selectRandomFromList(randomSelectionLines));
        } else if (type == Scene.SceneType.SELECTOR) {
            for (SceneLine line : lines) {
                if (line.shouldShow(context, lastSceneID)) {
                    validLines.add(line);
                    break;
                }
            }
        } else {
            validLines.addAll(lines);
        }
        return validLines;
    }

}
