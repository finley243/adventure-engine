package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class GameLoopManager {

    private void startRound() {
        if (!continueGame) return;
        eventBus.post(new TextClearEvent());
        TextGen.clearContext();
        for (Timer timer : timerManager.getAll()) {
            timer.update();
            if (timer.shouldRemove()) {
                timerManager.remove(timer.getID());
            }
        }
        for (Area area : areaRegistry.getAll()) {
            area.onStartRound(this);
        }
        for (WorldObject object : objectRegistry.getAll()) {
            object.onStartRound(this);
        }
        Actor player = actorRegistry.getPlayer();
        if (player.getArea().getRoom() != null) {
            player.getArea().getRoom().triggerScript("on_player_round", Context.builder().build());
        }
        player.getArea().triggerScript("on_player_round", Context.builder().build());
        // TODO - Add reverse function to get all actors that can see the player (for now, visibility is always mutual)
        for (Actor visibleActor : player.getLineOfSightActors(this)) {
            if (visibleActor.isVisible(player)) {
                visibleActor.triggerScript("on_player_visible_round", Context.builder().subject(visibleActor).build());
            }
        }
        dateTimeController.onNextRound();
        this.turnOrder = computeTurnOrder();
        this.currentTurnIndex = 0;
        nextTurn();
    }

}
