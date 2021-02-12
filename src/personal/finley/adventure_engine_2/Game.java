package personal.finley.adventure_engine_2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.common.eventbus.EventBus;

import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.actor.ControllerPlayer;
import personal.finley.adventure_engine_2.actor.ControllerUtility;
import personal.finley.adventure_engine_2.actor.IController;
import personal.finley.adventure_engine_2.load.WorldLoader;
import personal.finley.adventure_engine_2.textgen.LangUtils;
import personal.finley.adventure_engine_2.textgen.Phrases;
import personal.finley.adventure_engine_2.textgen.TextPrinter;
import personal.finley.adventure_engine_2.textgen.TextPrinter.Pronoun;

public class Game {
	
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static final String GAMEFILES = "src/gamefiles";
	public static final String WORLD_DIRECTORY = "/world";
	public static final String ACTOR_DIRECTORY = "/actors";
	public static final String ITEM_DIRECTORY = "/items";
	
	public static final String PLAYER_ACTOR = "player";
	public static final String PLAYER_START_AREA = "pallasCubiclesWindows";
	
	private TextPrinter printer;
	
	private boolean continueGameLoop;
	
	public Game() throws ParserConfigurationException, SAXException, IOException {
		printer = new TextPrinter();
		EVENT_BUS.register(printer);
		
		Phrases.load();
		
		WorldLoader.loadWorld(new File(GAMEFILES + WORLD_DIRECTORY));
		
		IController playerController = new ControllerPlayer();
		IController npcController = new ControllerUtility();
		
		Actor alpha = new Actor(PLAYER_ACTOR, "Alpha", true, Pronoun.YOU, PLAYER_START_AREA, null, null, false, playerController);
		Data.addActor(alpha.getID(), alpha);
		//Actor beta = new Actor("character2", "Beta", true, Pronoun.HE, PLAYER_START_AREA, null, null, false, npcController);
		//Actor gamma = new Actor("character3", "Gamma", true, Pronoun.SHE, PLAYER_START_AREA, null, null, false, npcController);
		//Actor delta = new Actor("character4", "Delta", true, Pronoun.IT, PLAYER_START_AREA, null, null, false, npcController);
		
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
