package com.github.finley243.adventureengine;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.DisplayTextEvent;
import com.github.finley243.adventureengine.event.EndPlayerTurnEvent;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.handler.PerceptionHandler;
import com.github.finley243.adventureengine.load.DialogueLoader;
import com.github.finley243.adventureengine.load.WorldLoader;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.ui.ConsoleInterface;
import com.github.finley243.adventureengine.ui.Gui;
import com.github.finley243.adventureengine.ui.UserInterface;
import com.github.finley243.adventureengine.world.template.ActorFactory;
import com.github.finley243.adventureengine.world.template.StatsActor;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Game {
	
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static final String PLAYER_ACTOR = "player";
	
	private static final String GAMEFILES = "src/gamefiles";
	private static final String WORLD_DIRECTORY = "/world";
	private static final String ACTOR_DIRECTORY = "/actors";
	private static final String ITEM_DIRECTORY = "/items";
	private static final String DIALOGUE_DIRECTORY = "/dialogues";
	
	//private TextGeneratorOld printer;
	private PerceptionHandler perceptionHandler;
	private UserInterface userInterface;
	
	private boolean continueGameLoop;
	private boolean waitingPlayerTurn;
	
	public Game() throws ParserConfigurationException, SAXException, IOException {
		//printer = new TextGeneratorOld();
		perceptionHandler = new PerceptionHandler();
		userInterface = new ConsoleInterface();
		//EVENT_BUS.register(printer);
		EVENT_BUS.register(perceptionHandler);
		EVENT_BUS.register(userInterface);
		EVENT_BUS.register(this);
		
		Phrases.load();
		WorldLoader.loadWorld(new File(GAMEFILES + WORLD_DIRECTORY));
		DialogueLoader.loadDialogue(new File(GAMEFILES + DIALOGUE_DIRECTORY));
		
		StatsActor playerStats = new StatsActor("Alpha", true, Pronoun.YOU);
		Actor player = ActorFactory.createPlayer(PLAYER_ACTOR, "stratis_hotel_lobby_entry", playerStats);
		player.addMoney(500);
		Data.addActor(player.getID(), player);
		StatsActor genericPassiveStats = new StatsActor("receptionist", false, Pronoun.HE);
		Actor stratisReceptionist = ActorFactory.create("stratisReceptionist", "stratis_hotel_lobby_desk", genericPassiveStats, "stratis_receptionist_start");
		Data.addActor(stratisReceptionist.getID(), stratisReceptionist);
		
		startGameLoop();
	}
	
	private void startGameLoop() {
		continueGameLoop = true;
		waitingPlayerTurn = false;
		while(continueGameLoop) {
			if(!waitingPlayerTurn) {
				round();
			} else {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void round() {
		String locationName = Data.getPlayer().getArea().getName();
		String roomName = Data.getPlayer().getArea().getRoom().getName();
		System.out.println("Location: " + LangUtils.titleCase(roomName) + " (" + LangUtils.titleCase(locationName) + ")");
		System.out.println();
		for(Actor actor : Data.getActors()) {
			if(!(actor instanceof ActorPlayer)) {
				actor.takeTurn();
			}
		}
		EVENT_BUS.post(new DisplayTextEvent(""));
		waitingPlayerTurn = true;
		Data.getPlayer().takeTurn();
	}
	
	@Subscribe
	public void onEndPlayerTurn(EndPlayerTurnEvent event) {
		waitingPlayerTurn = false;
	}
	
	private void endGameLoop() {
		continueGameLoop = false;
	}
	
}
