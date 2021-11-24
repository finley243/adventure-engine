package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.handler.PerceptionHandler;
import com.github.finley243.adventureengine.load.*;
import com.github.finley243.adventureengine.menu.ThreadControl;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.ui.ConsoleInterface;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceNested;
import com.github.finley243.adventureengine.ui.UserInterface;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemDistraction;
import com.github.finley243.adventureengine.world.template.ActorFactory;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class Game {
	
	public static final EventBus EVENT_BUS = new EventBus();
	
	private static final String GAMEFILES = "src/gamefiles";
	private static final String DATA_DIRECTORY = "/data";
	private static final String PHRASE_FILE = "/phrases.txt";
	private static final String CONFIG_FILE = "/config.xml";
	
	private final PerceptionHandler perceptionHandler;
	private final UserInterface userInterface;
	
	private boolean continueGameLoop;

	public static final ThreadControl THREAD_CONTROL = new ThreadControl();

	/** Main game constructor, loads data and starts game loop */
	public Game() throws ParserConfigurationException, SAXException, IOException {
		Phrases.load(new File(GAMEFILES + PHRASE_FILE));
		ConfigLoader.loadConfig(new File(GAMEFILES + CONFIG_FILE));
		DataLoader.loadFromDir(new File(GAMEFILES + DATA_DIRECTORY));

		perceptionHandler = new PerceptionHandler();
		userInterface = new GraphicalInterfaceNested();
		EVENT_BUS.register(perceptionHandler);
		EVENT_BUS.register(userInterface);
		EVENT_BUS.register(this);
		
		Actor player = ActorFactory.createPlayer(Data.getConfig("playerID"), Data.getArea(Data.getConfig("playerStartArea")), Data.getActorStats(Data.getConfig("playerStats")));
		Data.addActor(player.getID(), player);
		player.adjustMoney(320);

		Item coin = new ItemDistraction("coin");
		Data.getPlayer().inventory().addItem(coin);

		startGameLoop();
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
		EVENT_BUS.post(new TextClearEvent());
		TextGen.clearContext();
		SceneManager.trigger(Data.getPlayer().getArea().getRoom().getScenes());
		for(Actor actor : Data.getActors()) {
			if(!(actor instanceof ActorPlayer)) {
				CombatHelper.newTurn();
				actor.takeTurn();
			}
		}
		CombatHelper.newTurn();
		Data.getPlayer().takeTurn();
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
