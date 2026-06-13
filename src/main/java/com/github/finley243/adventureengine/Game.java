package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.TurnController;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.gamedata.ActorRegistry;
import com.github.finley243.adventureengine.gamedata.AreaRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.gamedata.TimerManager;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.quest.QuestManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.ui.*;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class Game {

	private final UIEventBus eventBus;
	private final MenuManager menuManager;

	//private final Data data;
	private final QuestManager questManager;
	private final DateTimeController dateTimeController;
	private final TimerManager timerManager;
	private final ActorRegistry actorRegistry;
	private final Registry<WorldObject> objectRegistry;
	private final AreaRegistry areaRegistry;
	private final ScriptRuntime scriptRuntime;
	private final Pathfinder pathfinder;
	private final TextGen textGen;
	private final TurnController turnController;

	private boolean continueGame;
	private List<Actor> turnOrder;
	private int currentTurnIndex;

	/** Main game constructor, loads data and starts game loop */
	public Game(UIEventBus eventBus, MenuManager menuManager, QuestManager questManager, DateTimeController dateTimeController, ScriptRuntime scriptRuntime, ActorRegistry actorRegistry, Registry<WorldObject> objectRegistry, AreaRegistry areaRegistry, TimerManager timerManager, Pathfinder pathfinder, TextGen textGen, TurnController turnController) {
		this.eventBus = eventBus;
		this.menuManager = menuManager;
		this.questManager = questManager;
		this.dateTimeController = dateTimeController;
		this.scriptRuntime = scriptRuntime;
		this.actorRegistry = actorRegistry;
		this.objectRegistry = objectRegistry;
		this.areaRegistry = areaRegistry;
		this.timerManager = timerManager;
		this.pathfinder = pathfinder;
		this.textGen = textGen;
		this.turnController = turnController;

		//data().newGame();
	}

	public void start() {
		continueGame = true;
		startRound();
	}

	private void startRound() {
		if (!continueGame) return;
		eventBus.post(new TextClearEvent());
		textGen.clearContext();
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
		this.turnOrder = computeTurnOrder();
		this.currentTurnIndex = 0;
		if (actorRegistry.getPlayer().isDead()) {
			continueGame = false;
		}
		nextTurn();
	}

	private void nextTurn() {
		turnController.takeTurn(turnOrder.get(currentTurnIndex), pathfinder, scriptRuntime, menuManager, questManager, this);
	}

	public void onEndTurn(Actor actor) {
		if (turnOrder.get(currentTurnIndex).equals(actor)) {
			currentTurnIndex += 1;
			if (currentTurnIndex >= turnOrder.size()) {
				startRound();
			} else {
				nextTurn();
			}
		}
	}

	private List<Actor> computeTurnOrder() {
		List<Actor> actors = new ArrayList<>(actorRegistry.getAll());
		actors.sort((a1, a2) -> a2.getAttribute("agility", Context.builder().subject(a2).target(a2).build()) - a1.getAttribute("agility", Context.builder().subject(a1).target(a1).build()));
		return actors;
	}
	
}
