package com.github.finley243.adventureengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.menu.EventQueue;
import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.handler.PerceptionHandler;
import com.github.finley243.adventureengine.load.ActorLoader;
import com.github.finley243.adventureengine.load.ConfigLoader;
import com.github.finley243.adventureengine.load.DialogueLoader;
import com.github.finley243.adventureengine.load.FactionLoader;
import com.github.finley243.adventureengine.load.ItemLoader;
import com.github.finley243.adventureengine.load.LootTableLoader;
import com.github.finley243.adventureengine.load.SceneLoader;
import com.github.finley243.adventureengine.load.WorldLoader;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceNested;
import com.github.finley243.adventureengine.ui.UserInterface;
import com.github.finley243.adventureengine.world.template.ActorFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@SuppressWarnings("UnstableApiUsage")
public class Game {
	
	public static final EventBus EVENT_BUS = new EventBus();
	
	private static final String GAMEFILES = "src/gamefiles";
	private static final String WORLD_DIRECTORY = "/world";
	private static final String ACTOR_DIRECTORY = "/actors";
	private static final String FACTION_DIRECTORY = "/factions";
	private static final String ITEM_DIRECTORY = "/items";
	private static final String DIALOGUE_DIRECTORY = "/dialogues";
	private static final String LOOT_TABLE_DIRECTORY = "/loottables";
	private static final String SCENES_DIRECTORY = "/scenes";
	private static final String PHRASE_FILE = "/phrases.txt";
	private static final String CONFIG_FILE = "/config.xml";
	
	private final PerceptionHandler perceptionHandler;
	private final UserInterface userInterface;
	private final EventQueue eventQueue;
	
	private boolean continueGameLoop;

	private final List<Actor> turnOrder;
	private int currentTurn;
	
	/** Main game constructor, loads data and starts game loop */
	public Game() throws ParserConfigurationException, SAXException, IOException {
		perceptionHandler = new PerceptionHandler();
		userInterface = new GraphicalInterfaceNested();
		eventQueue = new EventQueue();
		EVENT_BUS.register(perceptionHandler);
		EVENT_BUS.register(userInterface);
		EVENT_BUS.register(eventQueue);
		EVENT_BUS.register(this);
		
		
		Phrases.load(new File(GAMEFILES + PHRASE_FILE));
		ConfigLoader.loadConfig(new File(GAMEFILES + CONFIG_FILE));
		ItemLoader.loadItems(new File(GAMEFILES + ITEM_DIRECTORY));
		LootTableLoader.loadTables(new File(GAMEFILES + LOOT_TABLE_DIRECTORY));
		FactionLoader.loadFactions(new File(GAMEFILES + FACTION_DIRECTORY));
		DialogueLoader.loadDialogue(new File(GAMEFILES + DIALOGUE_DIRECTORY));
		ActorLoader.loadActors(new File(GAMEFILES + ACTOR_DIRECTORY));
		WorldLoader.loadWorld(new File(GAMEFILES + WORLD_DIRECTORY));
		SceneLoader.loadScenes(new File(GAMEFILES + SCENES_DIRECTORY));
		
		Actor player = ActorFactory.createPlayer(Data.getConfig("playerID"), Data.getArea(Data.getConfig("playerStartArea")), Data.getActorStats(Data.getConfig("playerStats")));
		Data.addActor(player.getID(), player);
		player.adjustMoney(320);

		turnOrder = new ArrayList<>(Data.getActors().size());

		startGameLoop();
	}
	
	/** Simple game loop that runs nextRound until continueGameLoop is false */
	private void startGameLoop() {
		continueGameLoop = true;
		//while(continueGameLoop) {
			//nextRound();
		//}
		nextRound();
	}
	
	/** Executes a single round of the game (every actor takes a turn) */
	private void nextRound() {
		EVENT_BUS.post(new TextClearEvent());
		TextGen.clearContext();
		EVENT_BUS.post(new RenderLocationEvent());
		//Data.getPlayer().triggerSceneManager();
		/*for(Actor actor : Data.getActors()) {
			if(!(actor instanceof ActorPlayer)) {
				actor.takeTurn();
			}
		}
		Data.getPlayer().takeTurn();
		sleep(800);
		EVENT_BUS.post(new TextClearEvent());
		TextGen.clearContext();*/
		updateTurnOrder();
		currentTurn = 0;
		startTurn();
		//Game.nextQueueEvent();
	}

	private void updateTurnOrder() {
		turnOrder.clear();
		for(Actor actor : Data.getActors()) {
			if(!(actor instanceof ActorPlayer)) {
				turnOrder.add(actor);
			}
		}
		turnOrder.add(Data.getPlayer());
		currentTurn = 0;
	}

	private void startTurn() {
		System.out.println("Start actor turn: " + turnOrder.get(currentTurn).getName());
		Actor actor = turnOrder.get(currentTurn);
		actor.takeTurn();
	}

	@Subscribe
	private void nextAction(NextActionEvent event) {
		Actor actor = turnOrder.get(currentTurn);
		actor.takeTurnAction();
	}

	@Subscribe
	private void onPlayerActionSelectEvent(PlayerActionSelectEvent event) {
		if(turnOrder.get(currentTurn) instanceof ActorPlayer) {
			turnOrder.get(currentTurn).handleActionSelection(event.getAction());
		}
	}

	@Subscribe
	private void endTurn(NextTurnEvent event) {
		currentTurn++;
		if(currentTurn == turnOrder.size()) {
			nextRound();
		} else {
			startTurn();
		}
	}
	
	/*private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/
	
	/** Ends the game loop, triggered when the player dies */
	@Subscribe
	private void endGameLoop(PlayerDeathEvent event) {
		continueGameLoop = false;
	}
	
	@Subscribe
	private void onRenderLocationEvent(RenderLocationEvent event) {
		ActorPlayer player = Data.getPlayer();
		String locationName = player.getArea().getName();
		String roomName = player.getArea().getRoom().getName();
		Game.EVENT_BUS.post(new RenderTextEvent("------------------------------"));
		Game.EVENT_BUS.post(new RenderTextEvent("Location: " + LangUtils.titleCase(roomName) + " (" + LangUtils.titleCase(locationName) + ")"));
		Game.EVENT_BUS.post(new RenderTextEvent(""));
	}

	public static void queueEvent(Object event) {
		Game.EVENT_BUS.post(new QueueEvent(event));
		//Game.EVENT_BUS.post(new NextQueueEvent());
	}

	public static void nextQueueEvent() {
		Game.EVENT_BUS.post(new NextQueueEvent());
	}
	
}
