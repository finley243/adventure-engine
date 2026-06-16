package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.controller.NPCTurnController;
import com.github.finley243.adventureengine.actor.controller.PlayerTurnController;
import com.github.finley243.adventureengine.actor.controller.TurnController;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.gamedata.ActorRegistry;
import com.github.finley243.adventureengine.gamedata.AreaRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.gamedata.TimerManager;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.quest.QuestManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class Game {

	private final UIEventBus eventBus;

    //private final Data data;
	private final QuestManager questManager;
	private final DateTimeController dateTimeController;
	private final TimerManager timerManager;
	private final ActorRegistry actorRegistry;
	private final Registry<WorldObject> objectRegistry;
	private final AreaRegistry areaRegistry;
	private final ScriptRuntime scriptRuntime;
	private final Pathfinder pathfinder;
    private final Map<Actor, TurnController> actorControllers;

	private boolean continueGame;

    public Game(UIEventBus eventBus, MenuManager menuManager, QuestManager questManager, DateTimeController dateTimeController, ScriptRuntime scriptRuntime, ActorRegistry actorRegistry, Registry<WorldObject> objectRegistry, AreaRegistry areaRegistry, TimerManager timerManager, Pathfinder pathfinder, SensoryEventDispatcher sensoryEventDispatcher) {
		this.eventBus = eventBus;
        this.questManager = questManager;
		this.dateTimeController = dateTimeController;
		this.scriptRuntime = scriptRuntime;
		this.actorRegistry = actorRegistry;
		this.objectRegistry = objectRegistry;
		this.areaRegistry = areaRegistry;
		this.timerManager = timerManager;
		this.pathfinder = pathfinder;
        this.actorControllers = new HashMap<>();
		for (Actor actor : actorRegistry.getAll()) {
			TurnController controller;
			if (actor.isPlayer()) {
				controller = new PlayerTurnController(actor, sensoryEventDispatcher, menuManager, eventBus, areaRegistry, () -> continueGame = false);
			} else {
				controller = new NPCTurnController(actor, sensoryEventDispatcher, menuManager);
			}
			actorControllers.put(actor, controller);
		}
	}

	public void start() {
		continueGame = true;
		while (continueGame) {
			startRound();
		}
	}

	private void startRound() {
		eventBus.post(new TextClearEvent());
		for (Timer timer : timerManager.getAll()) {
			timer.update();
			if (timer.shouldRemove()) {
				timerManager.remove(timer.getID());
			}
		}
		for (Area area : areaRegistry.getAll()) {
			area.onStartRound();
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
		for (Actor visibleActor : player.getLineOfSightActors(pathfinder)) {
			if (visibleActor.isVisible(player)) {
				visibleActor.triggerScript("on_player_visible_round", Context.builder().subject(visibleActor).build());
			}
		}
		dateTimeController.onNextRound();
        List<Actor> actorTurnOrder = computeTurnOrder();
		if (actorRegistry.getPlayer().isDead()) {
			continueGame = false;
		}
		for (Actor actor : actorTurnOrder) {
			TurnController controller = actorControllers.get(actor);
			controller.takeTurn(pathfinder, scriptRuntime, questManager);
		}
	}

	private List<Actor> computeTurnOrder() {
		List<Actor> actors = new ArrayList<>(actorRegistry.getAll());
		actors.sort((a1, a2) -> a2.getAttribute("agility", Context.builder().subject(a2).target(a2).build()) - a1.getAttribute("agility", Context.builder().subject(a1).target(a1).build()));
		return actors;
	}
	
}
