package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneChoice;

import java.util.ArrayList;
import java.util.List;

public class SceneChoiceMenuEvent implements QueuedEvent, ChoiceMenuEvent {

    private final Scene scene;
    private final List<SceneChoice> choices;
    private final Context context;
    private List<SceneChoice> validChoices;

    public SceneChoiceMenuEvent(Scene scene, List<SceneChoice> choices, Context context) {
        this.scene = scene;
        this.choices = choices;
        this.context = context;
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    public void execute(Game game) {
        List<SceneChoice> validChoices = new ArrayList<>();
        for (SceneChoice choice : choices) {
            if (game.data().getScene(choice.getLinkedID()).canChoose(context)) {
                validChoices.add(choice);
            }
        }
        if (validChoices.isEmpty()) {
            return;
        }
        this.validChoices = validChoices;
        game.menuManager().sceneChoiceMenu(this, game, validChoices);
    }

    @Override
    public boolean continueAfterExecution() {
        return validChoices.isEmpty();
    }

    @Override
    public void onChoiceMenuInput(int menuIndex) {
        context.game().eventBus().post(new TextClearEvent());
        context.game().eventQueue().addToFront(new SceneEvent(context.game().data().getScene(validChoices.get(menuIndex).getLinkedID()), scene.getID(), context));
        context.game().eventQueue().startExecution();
    }

}
