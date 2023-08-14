package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneChoice;

import java.util.List;

public class SceneChoiceEvent implements QueuedEvent {

    private final Scene scene;
    private final List<SceneChoice> choices;
    private final Context context;

    public SceneChoiceEvent(Scene scene, List<SceneChoice> choices, Context context) {
        this.scene = scene;
        this.choices = choices;
        this.context = context;
    }

    public Scene getScene() {
        return scene;
    }

    public List<SceneChoice> getChoices() {
        return choices;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void execute(Game game) {
        game.menuManager().sceneChoiceMenu(this);
    }

    public void onMenuInput(SceneChoice choice) {
        context.game().eventBus().post(new TextClearEvent());
        context.game().eventQueue().addToFront(new SceneEvent(context.game().data().getScene(choice.getLinkedId()), scene.getID(), context));
        context.game().eventQueue().executeNext();
    }

}
