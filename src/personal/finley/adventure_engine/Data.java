package personal.finley.adventure_engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.dialogue.Topic;
import personal.finley.adventure_engine.world.environment.Area;
import personal.finley.adventure_engine.world.environment.Room;
import personal.finley.adventure_engine.world.object.ObjectBase;

public class Data {
	
	private static Map<String, Boolean> globalFlags = new HashMap<String, Boolean>();
	private static Map<String, Area> areas = new HashMap<String, Area>();
	private static Map<String, Room> rooms = new HashMap<String, Room>();
	private static Map<String, Actor> actors = new HashMap<String, Actor>();
	private static Map<String, ObjectBase> objects = new HashMap<String, ObjectBase>();
	//private static Map<String, Item> items = new HashMap<String, Item>();
	private static Map<String, Topic> topics = new HashMap<String, Topic>(); 
	
	public static Actor getPlayer() {
		return actors.get(Game.PLAYER_ACTOR);
	}
	
	public static void setGlobalFlag(String id, boolean value) {
		globalFlags.put(id, value);
	}
	
	public static boolean getGlobalFlag(String id) {
		return globalFlags.get(id);
	}
	
	public static void addArea(String id, Area value) {
		areas.put(id, value);
	}
	
	public static Area getArea(String id) {
		return areas.get(id);
	}
	
	public static void addRoom(String id, Room value) {
		rooms.put(id, value);
	}
	
	public static Room getRoom(String id) {
		return rooms.get(id);
	}
	
	public static void addActor(String id, Actor value) {
		actors.put(id, value);
	}
	
	public static Actor getActor(String id) {
		return actors.get(id);
	}
	
	public static Collection<Actor> getActors() {
		return actors.values();
	}
	
	public static void addObject(String id, ObjectBase value) {
		objects.put(id, value);
	}
	
	public static ObjectBase getObject(String id) {
		return objects.get(id);
	}
	
	/*
	public static void addItem(String id, Item value) {
		items.put(id, value);
	}
	
	public static Item getItem(String id) {
		return items.get(id);
	}
	*/
	
	public static void addTopic(String id, Topic value) {
		topics.put(id, value);
	}
	
	public static Topic getTopic(String id) {
		return topics.get(id);
	}
	
}
