package personal.finley.adventure_engine_2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.common.eventbus.EventBus;

import personal.finley.adventure_engine_2.EnumTypes.Pronoun;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.actor.ControllerPlayer;
import personal.finley.adventure_engine_2.actor.ControllerRandom;
import personal.finley.adventure_engine_2.actor.IController;
import personal.finley.adventure_engine_2.load.WorldLoader;
import personal.finley.adventure_engine_2.textgen.LangUtils;
import personal.finley.adventure_engine_2.textgen.Phrases;
import personal.finley.adventure_engine_2.textgen.TextPrinter;
import personal.finley.adventure_engine_2.world.environment.Area;
import personal.finley.adventure_engine_2.world.object.ObjectBase;
import personal.finley.adventure_engine_2.world.object.ObjectExit;

public class Game {
	
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static final String GAMEFILES = "src/gamefiles";
	public static final String AREA_DIRECTORY = "/areas";
	
	public static final String PLAYER_ACTOR = "player";
	public static final String PLAYER_START_AREA = "pallasCubiclesWindows";
	
	private TextPrinter printer;
	
	private boolean continueGameLoop;
	
	public Game() throws ParserConfigurationException, SAXException, IOException {
		printer = new TextPrinter();
		EVENT_BUS.register(printer);
		
		Phrases.load();
		
		WorldLoader.loadRooms(new File(GAMEFILES + AREA_DIRECTORY));
		
		IController playerController = new ControllerPlayer();
		IController npcController = new ControllerRandom();
		
		Actor alpha = new Actor(PLAYER_ACTOR, "Alpha", true, Pronoun.YOU, PLAYER_START_AREA, null, null, false, playerController);
		//Actor beta = new Actor("character2", "Beta", true, Pronoun.HE, PLAYER_START_AREA, null, null, false, npcController);
		//Actor gamma = new Actor("character3", "Gamma", true, Pronoun.SHE, PLAYER_START_AREA, null, null, false, npcController);
		//Actor delta = new Actor("character4", "Delta", true, Pronoun.IT, PLAYER_START_AREA, null, null, false, npcController);
		
		Area cubicles = Data.getArea("pallasCubiclesCopier");
		Area conference = Data.getArea("pallasConferenceEntrance");
		
		ObjectBase door1 = new ObjectExit("door1", "door", "door2");
		ObjectBase door2 = new ObjectExit("door2", "door", "door1");
		
		cubicles.addObject(door1);
		door1.setAreaID(cubicles.getID());
		conference.addObject(door2);
		door2.setAreaID(conference.getID());
		
		startGameLoop();
	}
	
	public void startGameLoop() {
		continueGameLoop = true;
		while(continueGameLoop) {
			String locationName = Data.getPlayer().getArea().getName();
			String roomName = Data.getPlayer().getArea().getRoom().getName();
			System.out.println("Player Location: " + LangUtils.titleCase(locationName) + " (" + LangUtils.titleCase(roomName) + ")");
			for(Actor actor : Data.getActors()) {
				actor.takeTurn();
			}
		}
	}
	
	public void endGameLoop() {
		continueGameLoop = false;
	}
	
}
