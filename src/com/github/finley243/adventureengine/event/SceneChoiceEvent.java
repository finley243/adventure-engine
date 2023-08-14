package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.scene.SceneChoice;

import java.util.List;

public class SceneChoiceEvent implements QueuedEvent {

    private final List<SceneChoice> choices;
    private final Context context;

    public SceneChoiceEvent(List<SceneChoice> choices, Context context) {
        this.choices = choices;
        this.context = context;
    }

    @Override
    public void execute(Game game) {

    }

}
