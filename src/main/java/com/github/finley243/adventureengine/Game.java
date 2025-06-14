package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.ConfigLoader;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.quest.QuestManager;
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
	public static final String CONFIG_FILE = "/config.xml";

	private final EventBus eventBus;
	private final MenuManager menuManager;

	private final Data data;
	private final QuestManager questManager;

	private boolean continueGame;
	private List<Actor> turnOrder;
	private int currentTurnIndex;

	/** Main game constructor, loads data and starts game loop */
	public Game() throws ParserConfigurationException, SAXException, IOException, GameDataException {
		eventBus = new EventBus();
		menuManager = new MenuManager(this);
		eventBus().register(menuManager);
		data = new Data(this);
		questManager = new QuestManager();

		ConfigLoader.loadConfig(this, new File(GAMEFILES + CONFIG_FILE));

		if (data.getConfig("enableDebugLog").equalsIgnoreCase("true")) {
			DebugLogger.init(GAMEFILES + LOG_DIRECTORY);
		}

		UserInterface userInterface = switch (data.getConfig("interfaceType")) {
			case "graphicalChoice" -> new GraphicalInterfaceComplex(eventBus, data.getConfig("gameName"));
			case "consoleParser" -> new ConsoleParserInterface(this);
            default -> new ConsoleInterface(this); // "consoleChoice"
		};
		eventBus().register(userInterface);

		data().newGame();

		data().getPlayer().setStatValue("money", Expression.constant(50), new Context(this, data().getPlayer(), data().getPlayer()));

		/*File saveFile = new File(GAMEFILES + "/save.asav");
		SaveLoader.saveGame(saveFile, data());
		System.out.println("Saved Game");
		try {
			SaveLoader.loadGame(saveFile, data());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded Game");*/

		continueGame = true;
		startRound();
	}

	public EventBus eventBus() {
		return eventBus;
	}

	public MenuManager menuManager() {
		return menuManager;
	}

	public Data data() {
		return data;
	}

	public QuestManager questManager() {
		return questManager;
	}

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
		if (data.getPlayer().getArea().getRoom() != null) {
			data().getPlayer().getArea().getRoom().triggerScript("on_player_round", data().getPlayer(), data().getPlayer());
		}
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
	
	/** Ends the game loop, triggered when the player dies */
	public void onPlayerDeath() {
		continueGame = false;
	}
	
}
