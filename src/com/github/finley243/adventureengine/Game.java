package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.handler.PerceptionHandler;
import com.github.finley243.adventureengine.load.*;
import com.github.finley243.adventureengine.menu.ThreadControl;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceNested;
import com.github.finley243.adventureengine.ui.UserInterface;
import com.github.finley243.adventureengine.actor.ActorFactory;
import com.github.finley243.adventureengine.world.item.ItemFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class Game {
	
	public static final String GAMEFILES = "src/gamefiles";
	public static final String DATA_DIRECTORY = "/data";
	public static final String PHRASE_FILE = "/phrases.txt";
	public static final String CONFIG_FILE = "/config.xml";

	private final EventBus eventBus;
	private final ThreadControl threadControl;

	private final Data data;

	private boolean continueGameLoop;

	/** Main game constructor, loads data and starts game loop */
	public Game() throws ParserConfigurationException, SAXException, IOException {
		eventBus = new EventBus();
		threadControl = new ThreadControl();
		data = new Data(this);

		Phrases.load(new File(GAMEFILES + PHRASE_FILE));
		ConfigLoader.loadConfig(this, new File(GAMEFILES + CONFIG_FILE));
		//DataLoader.loadFromDir(this, new File(GAMEFILES + DATA_DIRECTORY));

		PerceptionHandler perceptionHandler = new PerceptionHandler();
		UserInterface userInterface = new GraphicalInterfaceNested(this);
		eventBus.register(perceptionHandler);
		eventBus.register(userInterface);
		eventBus.register(this);

		data.newGame();

		data().getPlayer().adjustMoney(320);
		data().getPlayer().inventory().addItem(ItemFactory.create(this, "tactical_vest", null));
		data().getPlayer().inventory().addItem(ItemFactory.create(this, "tactical_helmet", null));

		File saveFile = new File(GAMEFILES + "/save.aes");
		SaveLoader.saveGame(saveFile, data());
		System.out.println("Saved Game");
		try {
			SaveLoader.loadGame(saveFile, data());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded Game");

		startGameLoop();
	}

	public EventBus eventBus() {
		return eventBus;
	}

	public ThreadControl threadControl() {
		return threadControl;
	}

	public Data data() {
		return data;
	}
	
	/** Simple game loop that runs nextRound until continueGameLoop is false */
	private void startGameLoop() {
		continueGameLoop = true;
		while(continueGameLoop) {
			nextRound();
			sleep(800);
		}
	}
	
	/** Executes a single round of the game (every actor takes a turn) */
	private void nextRound() {
		eventBus.post(new TextClearEvent());
		TextGen.clearContext();
		data().getPlayer().getArea().getRoom().triggerScript("on_player_round", data().getPlayer());
		data().getPlayer().getArea().triggerScript("on_player_round", data().getPlayer());
		// TODO - Add reverse function to get all actors that can see the player (for now, visibility is always mutual)
		for(Actor visibleActor : data().getPlayer().getVisibleActors()) {
			visibleActor.triggerScript("on_player_visible_round");
		}
		for(Actor actor : data().getActors()) {
			if(!(actor instanceof ActorPlayer)) {
				CombatHelper.newTurn();
				actor.takeTurn();
			}
		}
		CombatHelper.newTurn();
		data().getPlayer().describeSurroundings();
		System.out.println("Player max HP: " + data().getPlayer().getMaxHP().value());
		data().getPlayer().takeTurn();
	}
	
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** Ends the game loop, triggered when the player dies */
	@Subscribe
	private void endGameLoop(PlayerDeathEvent e) {
		continueGameLoop = false;
	}
	
}
