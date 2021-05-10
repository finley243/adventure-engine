package com.github.finley243.adventureengine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.handler.PerceptionHandler;
import com.github.finley243.adventureengine.load.DialogueLoader;
import com.github.finley243.adventureengine.load.ItemLoader;
import com.github.finley243.adventureengine.load.WorldLoader;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.ui.ConsoleInterface;
import com.github.finley243.adventureengine.ui.GraphicalInterface;
import com.github.finley243.adventureengine.ui.UserInterface;
import com.github.finley243.adventureengine.world.template.ActorFactory;
import com.github.finley243.adventureengine.world.template.ItemFactory;
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
	
	private PerceptionHandler perceptionHandler;
	private UserInterface userInterface;
	
	private boolean continueGameLoop;
	
	/** Main game constructor, loads data and starts game loop */
	public Game() throws ParserConfigurationException, SAXException, IOException {
		perceptionHandler = new PerceptionHandler();
		userInterface = new GraphicalInterface();
		EVENT_BUS.register(perceptionHandler);
		EVENT_BUS.register(userInterface);
		EVENT_BUS.register(this);
		
		Phrases.load();
		ItemLoader.loadItems(new File(GAMEFILES + ITEM_DIRECTORY));
		WorldLoader.loadWorld(new File(GAMEFILES + WORLD_DIRECTORY));
		DialogueLoader.loadDialogue(new File(GAMEFILES + DIALOGUE_DIRECTORY));
		
		Faction playerFaction = new Faction("player", FactionRelation.NEUTRAL, new HashMap<String, FactionRelation>());
		Data.addFaction(playerFaction.getID(), playerFaction);
		StatsActor playerStats = new StatsActor("PLAYER", true, Pronoun.YOU, playerFaction, 50, 3);
		Actor player = ActorFactory.createPlayer(PLAYER_ACTOR, "stratis_hotel_lobby_entry", playerStats);
		player.adjustMoney(320);
		Data.addActor(player.getID(), player);
		
		Faction stratisStaffFaction = new Faction("stratis_hotel_staff", FactionRelation.NEUTRAL, new HashMap<String, FactionRelation>());
		Data.addFaction(stratisStaffFaction.getID(), stratisStaffFaction);
		StatsActor genericPassiveStats = new StatsActor("receptionist", false, Pronoun.HE, stratisStaffFaction, 40, 3);
		Actor stratisReceptionist = ActorFactory.create("stratisReceptionist", "stratis_hotel_lobby_desk", genericPassiveStats, "stratis_receptionist_start");
		Data.addActor(stratisReceptionist.getID(), stratisReceptionist);
		
		player.inventory().addItem(ItemFactory.create("light_pistol"));
		player.inventory().addItem(ItemFactory.create("baseball_bat"));
		stratisReceptionist.inventory().addItem(ItemFactory.create("light_pistol"));
		
		startGameLoop();
	}
	
	/** Simple game loop that runs nextRound until continueGameLoop is false */
	private void startGameLoop() {
		continueGameLoop = true;
		while(continueGameLoop) {
			nextRound();
		}
	}
	
	/** Executes a single round of the game (every actor takes a turn) */
	private void nextRound() {
		String locationName = Data.getPlayer().getArea().getName();
		String roomName = Data.getPlayer().getArea().getRoom().getName();
		Game.EVENT_BUS.post(new RenderTextEvent("------------------------------"));
		Game.EVENT_BUS.post(new RenderTextEvent("Location: " + LangUtils.titleCase(roomName) + " (" + LangUtils.titleCase(locationName) + ")"));
		Game.EVENT_BUS.post(new RenderTextEvent(""));
		Data.getPlayer().updateRoomDescription();
		for(Actor actor : Data.getActors()) {
			if(!(actor instanceof ActorPlayer)) {
				actor.takeTurn();
			}
		}
		EVENT_BUS.post(new RenderTextEvent(""));
		Data.getPlayer().takeTurn();
	}
	
	/** Ends the game loop, triggered when the player dies */
	@Subscribe
	private void endGameLoop(PlayerDeathEvent event) {
		continueGameLoop = false;
	}
	
}
