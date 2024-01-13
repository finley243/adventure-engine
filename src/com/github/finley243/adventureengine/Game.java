package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.ConfigLoader;
import com.github.finley243.adventureengine.load.ScriptParser;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.menu.ThreadControl;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.ui.*;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.google.common.eventbus.EventBus;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class Game {
	
	public static final String GAMEFILES = "src/gamefiles";
	public static final String DATA_DIRECTORY = "/data";
	public static final String LOG_DIRECTORY = "/logs";
	public static final String PHRASE_FILE = "/phrases.txt";
	public static final String CONFIG_FILE = "/config.xml";

	private final EventBus eventBus;
	private final EventQueue eventQueue;
	private final MenuManager menuManager;
	private final DebugLogger debugLogger;
	private final ThreadControl threadControl;

	private final Data data;

	private boolean continueGame;
	private List<Actor> turnOrder;
	private int currentTurnIndex;

	/** Main game constructor, loads data and starts game loop */
	public Game() throws ParserConfigurationException, SAXException, IOException, GameDataException {
		eventBus = new EventBus();
		eventQueue = new EventQueue(this);
		menuManager = new MenuManager();
		threadControl = new ThreadControl(this);
		eventBus().register(menuManager);
		data = new Data(this);

		Phrases.load(new File(GAMEFILES + PHRASE_FILE));
		ConfigLoader.loadConfig(this, new File(GAMEFILES + CONFIG_FILE));

		debugLogger = new DebugLogger(GAMEFILES + LOG_DIRECTORY, data.getConfig("enableDebugLog").equalsIgnoreCase("true"));

		UserInterface userInterface = switch (data.getConfig("interfaceType")) {
			case "graphicalChoice" -> new GraphicalInterfaceComplex(this);
			case "consoleParser" -> new ConsoleParserInterface(this);
            default -> new ConsoleInterface(this); // "consoleChoice"
		};
		eventBus().register(userInterface);

		data().newGame();

		data().getPlayer().setStatValue("money", Expression.constant(50), new Context(this, data().getPlayer(), data().getPlayer()));

		/*File saveFile = new File(GAMEFILES + "/save.aes");
		SaveLoader.saveGame(saveFile, data());
		System.out.println("Saved Game");
		try {
			SaveLoader.loadGame(saveFile, data());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded Game");*/

		//System.out.println(Pathfinder.getLineOfSightAreas(data().getArea("apartment_0_3")));
		ScriptParser.parseScripts("func drop_equipped_force_specified_slot(equippedSlot) {\n" +
				"\t//condition stat.subject.(\"has_equipped_\" + equippedSlot);\n" +
				"\tif (stat.subject.(\"has_equipped_\" + equippedSlot)) {\n" +
				"\t\tvar dropAreaID = randomStringFromSet(stat.subject.area.movable_areas);\n" +
				"\t\tvar equippedItemID = stat.subject.equipped_item(equippedSlot).id;\n" +
				"\t\ttransferItem(type=instance, from=stat.subject.inv, to=stat.area(dropAreaID).inv, item=equippedItemID);\n" +
				"\t\tsensoryEvent(phrase=\"forceDrop\", area=stat.subject.area.id);\n" +
				"\t}\n" +
				"}");
		//ScriptParser.parseScripts("\"test\" \"'single quotes'\" \"\\\"escaped quotes\\\"\"");

		continueGame = true;
		startRound();
	}

	public EventBus eventBus() {
		return eventBus;
	}

	public EventQueue eventQueue() {
		return eventQueue;
	}

	public MenuManager menuManager() {
		return menuManager;
	}

	public ThreadControl threadControl() {
		return threadControl;
	}

	public DebugLogger log() {
		return debugLogger;
	}

	public Data data() {
		return data;
	}
	
	/** Simple game loop that runs nextRound until continueGameLoop is false */
	/*private void startGameLoop() {
		continueGame = true;
		while (continueGame) {
			nextRound();
			if (data().getPlayer().isActive()) {
				sleep(800);
			}
		}
	}*/

	private void startRound() {
		if (!continueGame) return;
		eventBus.post(new TextClearEvent());
		TextGen.clearContext();
		for (Timer timer : data().getTimers()) {
			timer.update();
			if (timer.shouldRemove()) {
				data().removeTimer(timer.getID());
			}
		}
		for (Area area : data().getAreas()) {
			area.onStartRound();
		}
		for (WorldObject object : data().getObjects()) {
			object.onStartRound();
		}
		data().getPlayer().getArea().getRoom().triggerScript("on_player_round", data().getPlayer(), data().getPlayer());
		data().getPlayer().getArea().triggerScript("on_player_round", data().getPlayer(), data().getPlayer());
		// TODO - Add reverse function to get all actors that can see the player (for now, visibility is always mutual)
		for (Actor visibleActor : data().getPlayer().getLineOfSightActors()) {
			if (visibleActor.isVisible(data().getPlayer())) {
				visibleActor.triggerScript("on_player_visible_round", new Context(this, visibleActor, data().getPlayer()));
			}
		}
		data().dateTime().onNextRound();
		this.turnOrder = computeTurnOrder();
		this.currentTurnIndex = 0;
		nextTurn();
		eventQueue.startExecution();
	}

	private void nextTurn() {
		turnOrder.get(currentTurnIndex).takeTurn();
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
		List<Actor> actors = new ArrayList<>(data().getActors());
		actors.sort((a1, a2) -> a2.getAttribute("agility", new Context(this, a2, a2)) - a1.getAttribute("agility", new Context(this, a1, a1)));
		return actors;
	}
	
	/** Executes a single round of the game (every actor takes a turn) */
	/*private void nextRound() {
		eventBus.post(new TextClearEvent());
		TextGen.clearContext();
		for (Timer timer : data().getTimers()) {
			timer.update();
			if (timer.shouldRemove()) {
				data().removeTimer(timer.getID());
			}
		}
		for (Area area : data().getAreas()) {
			area.onStartRound();
		}
		for (WorldObject object : data().getObjects()) {
			object.onStartRound();
		}
		data().getPlayer().getArea().getRoom().triggerScript("on_player_round", data().getPlayer(), data().getPlayer());
		data().getPlayer().getArea().triggerScript("on_player_round", data().getPlayer(), data().getPlayer());
		// TODO - Add reverse function to get all actors that can see the player (for now, visibility is always mutual)
		for (Actor visibleActor : data().getPlayer().getVisibleActors()) {
			visibleActor.triggerScript("on_player_visible_round", new Context(this, visibleActor, data().getPlayer()));
		}
		for (Actor actor : data().getActors()) {
			if (!actor.isPlayer()) {
				actor.takeTurn();
			}
		}
		data().getPlayer().takeTurn();
		data().dateTime().onNextRound();
	}*/
	
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** Ends the game loop, triggered when the player dies */
	public void onPlayerDeath() {
		continueGame = false;
	}
	
}
