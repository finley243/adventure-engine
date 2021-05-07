package com.github.finley243.adventureengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.dialogue.Topic;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.LinkedObject;
import com.github.finley243.adventureengine.world.template.StatsItem;

public class Data {
	
	private static Map<String, Boolean> globalFlags = new HashMap<String, Boolean>();
	private static Map<String, Area> areas = new HashMap<String, Area>();
	private static Map<String, Room> rooms = new HashMap<String, Room>();
	private static Map<String, Actor> actors = new HashMap<String, Actor>();
	private static Map<String, LinkedObject> linkedObjects = new HashMap<String, LinkedObject>();
	private static Map<String, StatsItem> items = new HashMap<String, StatsItem>();
	private static Map<String, Topic> topics = new HashMap<String, Topic>();
	private static Set<String> knowledge = new HashSet<String>();
	
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
	
	public static void addLinkedObject(String id, LinkedObject value) {
		linkedObjects.put(id, value);
	}
	
	public static LinkedObject getLinkedObject(String id) {
		return linkedObjects.get(id);
	}
	
	public static void addItem(String id, StatsItem value) {
		items.put(id, value);
	}
	
	public static StatsItem getItem(String id) {
		return items.get(id);
	}
	
	public static void addTopic(String id, Topic value) {
		topics.put(id, value);
	}
	
	public static Topic getTopic(String id) {
		return topics.get(id);
	}
	
	public static void addKnowledge(String value) {
		knowledge.add(value);
	}
	
	public boolean hasKnowledge(String value) {
		return knowledge.contains(value);
	}
	
}
