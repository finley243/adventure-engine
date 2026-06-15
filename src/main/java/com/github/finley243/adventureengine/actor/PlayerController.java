package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.gamedata.AreaRegistry;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.Collection;
import java.util.List;

public class PlayerController extends TurnController {

    private final UIEventBus eventBus;
    private final AreaRegistry areaRegistry;
    private final Runnable onGameEnd;

    private Area lastKnownArea;

    public PlayerController(Actor actor, SensoryEventDispatcher sensoryEventDispatcher, MenuManager menuManager, UIEventBus eventBus, AreaRegistry areaRegistry, Runnable onGameEnd) {
        super(actor, sensoryEventDispatcher, menuManager);
        this.eventBus = eventBus;
        this.areaRegistry = areaRegistry;
        this.onGameEnd = onGameEnd;
    }

    @Override
    protected void onStartTurnDead() {
        onPlayerDeath();
    }

    @Override
    protected void onPostAction(Action action) {
        if (actor.isDead()) {
            onPlayerDeath();
        }
        Area currentArea = actor.getArea();
        if (currentArea != lastKnownArea) {
            boolean isNewRoom = lastKnownArea == null || currentArea.getRoom() != lastKnownArea.getRoom();
            onPlayerEnterArea(currentArea, isNewRoom);
            lastKnownArea = currentArea;
        }
    }

    @Override
    protected Action selectAction(List<Action> actions) {
        return menuManager.actionChoiceMenu(actor, this, actions);
    }

    private void onPlayerDeath() {
        onGameEnd.run();
    }

    private void onPlayerEnterArea(Area area, boolean isNewRoom) {
        eventBus.post(new RenderAreaEvent(area.getRoom() != null ? LangUtils.titleCase(area.getRoom().getName()) : null, LangUtils.titleCase(area.getName())));
        Context context = Context.builder().subject(actor).parentArea(area).build();
        if (isNewRoom && area.getRoom() != null && area.getRoom().getDescription() != null) {
            menuManager.sceneMenu(area.getRoom().getDescription(), context, false);
            area.getRoom().setKnown();
            Collection<Area> areasInRoom = areaRegistry.getAllInRoomID(area.getRoom().getID());
            for (Area areaInRoom : areasInRoom) {
                areaInRoom.setKnown();
            }
        }
        if (area.getDescription() != null) {
            menuManager.sceneMenu(area.getDescription(), context, false);
            area.setKnown();
        }
        if (isNewRoom && area.getRoom() != null) {
            area.getRoom().triggerScript("on_player_enter", context);
        }
        area.triggerScript("on_player_enter", context);
    }

}
