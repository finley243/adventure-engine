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
import com.github.finley243.adventureengine.event.TextClearEvent;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.RenderLocationEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.handler.PerceptionHandler;
import com.github.finley243.adventureengine.load.DialogueLoader;
import com.github.finley243.adventureengine.load.FactionLoader;
import com.github.finley243.adventureengine.load.ItemLoader;
import com.github.finley243.adventureengine.load.LootTableLoader;
import com.github.finley243.adventureengine.load.WorldLoader;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.ui.ConsoleInterface;
import com.github.finley243.adventureengine.ui.GraphicalInterface;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceNested;
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
	private static final String FACTION_DIRECTORY = "/factions";
	private static final String ITEM_DIRECTORY = "/items";
	private static final String DIALOGUE_DIRECTORY = "/dialogues";
	private static final String LOOT_TABLE_DIRECTORY = "/loottables";
	
	private PerceptionHandler perceptionHandler;
	private UserInterface userInterface;
	
	private boolean continueGameLoop;
	
	/** Main game constructor, loads data and starts game loop */
	public Game() throws ParserConfigurationException, SAXException, IOException {
		perceptionHandler = new PerceptionHandler();
		userInterface = new GraphicalInterfaceNested();
		EVENT_BUS.register(perceptionHandler);
		EVENT_BUS.register(userInterface);
		EVENT_BUS.register(this);
		
		Phrases.load();
		ItemLoader.loadItems(new File(GAMEFILES + ITEM_DIRECTORY));
		WorldLoader.loadWorld(new File(GAMEFILES + WORLD_DIRECTORY));
		FactionLoader.loadFactions(new File(GAMEFILES + FACTION_DIRECTORY));
		DialogueLoader.loadDialogue(new File(GAMEFILES + DIALOGUE_DIRECTORY));
		LootTableLoader.loadTables(new File(GAMEFILES + LOOT_TABLE_DIRECTORY));
		
		StatsActor playerStats = new StatsActor("PLAYER", true, Pronoun.YOU, Data.getFaction("player"), 50, 2);
		Actor player = ActorFactory.createPlayer(PLAYER_ACTOR, "stratis_hotel_lobby_entry", playerStats);
		Data.addActor(player.getID(), player);
		player.adjustMoney(320);
		player.inventory().addItem(ItemFactory.create("light_pistol"));
		
		StatsActor genericPassiveStats = new StatsActor("receptionist", false, Pronoun.HE, Data.getFaction("stratis_hotel_staff"), 40, 2);
		Actor stratisReceptionist = ActorFactory.create("stratisReceptionist", "stratis_hotel_lobby_desk", genericPassiveStats, "stratis_receptionist_start");
		Data.addActor(stratisReceptionist.getID(), stratisReceptionist);
		//stratisReceptionist.inventory().addItem(ItemFactory.create("light_pistol"));
		stratisReceptionist.inventory().addItems(Data.getLootTable("weapon_basic").generateItems());
		
		StatsActor enemyStats = new StatsActor("ganger", false, Pronoun.HE, Data.getFaction("enemy"), 40, 2);
		Actor enemy = ActorFactory.create("enemy", "stratis_hotel_lobby_elevators", enemyStats, null);
		Data.addActor(enemy.getID(), enemy);
		//enemy.inventory().addItem(ItemFactory.create("light_pistol"));
		enemy.inventory().addItems(Data.getLootTable("weapon_basic").generateItems());
		
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
		EVENT_BUS.post(new RenderLocationEvent());
		Data.getPlayer().updateRoomDescription();
		for(Actor actor : Data.getActors()) {
			if(!(actor instanceof ActorPlayer)) {
				actor.takeTurn();
			}
		}
		Data.getPlayer().takeTurn();
		sleep(800);
		EVENT_BUS.post(new TextClearEvent());
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
	
}
