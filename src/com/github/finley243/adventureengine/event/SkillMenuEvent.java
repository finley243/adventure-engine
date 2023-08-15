package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.Map;

public class SkillMenuEvent implements QueuedEvent, NumericMenuEvent {

    private final Actor actor;
    private final int points;

    public SkillMenuEvent(Actor actor, int points) {
        this.actor = actor;
        this.points = points;
    }

    @Override
    public void execute(Game game) {
        game.menuManager().skillMenu(this, game, actor, points);
    }

    @Override
    public void onNumericMenuInput(Map<String, Integer> values) {
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            actor.setSkillBase(entry.getKey(), entry.getValue());
        }
    }

}
