package personal.finley.adventure_engine;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.common.eventbus.EventBus;

import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.gui.Gui;
import personal.finley.adventure_engine.load.DialogueLoader;
import personal.finley.adventure_engine.load.WorldLoader;
import personal.finley.adventure_engine.textgen.LangUtils;
import personal.finley.adventure_engine.textgen.Phrases;
import personal.finley.adventure_engine.textgen.TextGenerator;
import personal.finley.adventure_engine.textgen.Context.Pronoun;
import personal.finley.adventure_engine.world.template.ActorFactory;
import personal.finley.adventure_engine.world.template.StatsActor;

public class Game {
	
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static final String GAMEFILES = "src/gamefiles";
	public static final String WORLD_DIRECTORY = "/world";
	public static final String ACTOR_DIRECTORY = "/actors";
	public static final String ITEM_DIRECTORY = "/items";
	public static final String DIALOGUE_DIRECTORY = "/dialogues";
	
	public static final String PLAYER_ACTOR = "player";
	
	private TextGenerator printer;
	//private Gui gui;
	
	private boolean continueGameLoop;
	
	public Game() throws ParserConfigurationException, SAXException, IOException {
		printer = new TextGenerator();
		//gui = new Gui();
		EVENT_BUS.register(printer);
		
		Phrases.load();
		WorldLoader.loadWorld(new File(GAMEFILES + WORLD_DIRECTORY));
		DialogueLoader.loadDialogue(new File(GAMEFILES + DIALOGUE_DIRECTORY));
		
		StatsActor playerStats = new StatsActor("Alpha", true, Pronoun.YOU);
		Actor player = ActorFactory.createPlayer(PLAYER_ACTOR, "stratis_hotel_lobby_entry", playerStats);
		Data.addActor(player.getID(), player);
		StatsActor genericPassiveStats = new StatsActor("receptionist", false, Pronoun.HE);
		Actor stratisReceptionist = ActorFactory.create("stratisReceptionist", "stratis_hotel_lobby_desk", genericPassiveStats, "stratis_receptionist_start");
		Data.addActor(stratisReceptionist.getID(), stratisReceptionist);
		
		startGameLoop();
	}
	
	public void startGameLoop() {
		continueGameLoop = true;
		while(continueGameLoop) {
			String locationName = Data.getPlayer().getArea().getName();
			String roomName = Data.getPlayer().getArea().getRoom().getName();
			System.out.println("Location: " + LangUtils.titleCase(roomName) + " (" + LangUtils.titleCase(locationName) + ")");
			if(!Data.getPlayer().getArea().getRoom().hasVisited()) {
				System.out.println();
				System.out.println(Data.getPlayer().getArea().getRoom().getDescription());
				Data.getPlayer().getArea().getRoom().setVisited();
			}
			System.out.println();
			for(Actor actor : Data.getActors()) {
				actor.takeTurn();
			}
			System.out.println();
			System.out.println("---------------------------------------------------");
			System.out.println();
		}
	}
	
	public void endGameLoop() {
		continueGameLoop = false;
	}
	
}
