package personal.finley.adventure_engine_2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.dialogue.Topic;
import personal.finley.adventure_engine_2.world.environment.Area;
import personal.finley.adventure_engine_2.world.environment.Room;
import personal.finley.adventure_engine_2.world.object.ObjectBase;
import personal.finley.adventure_engine_2.world.object.item.Item;
import personal.finley.adventure_engine_2.world.object.item.template.ItemTemplate;

public class Data {
	
	private static Map<String, Boolean> globalFlags = new HashMap<String, Boolean>();
	private static Map<String, Area> areas = new HashMap<String, Area>();
	private static Map<String, Room> rooms = new HashMap<String, Room>();
	private static Map<String, Actor> actors = new HashMap<String, Actor>();
	private static Map<String, ObjectBase> objects = new HashMap<String, ObjectBase>();
	//private static Map<String, Item> items = new HashMap<String, Item>();
	private static Map<String, ItemTemplate> itemTemplates = new HashMap<String, ItemTemplate>();
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
	
	public static void addItemTemplate(String id, ItemTemplate value) {
		itemTemplates.put(id, value);
	}
	
	public static ItemTemplate getItemTemplate(String id) {
		return itemTemplates.get(id);
	}
	
	public static void addTopic(String id, Topic value) {
		topics.put(id, value);
	}
	
	public static Topic getTopic(String id) {
		return topics.get(id);
	}
	
}
