package com.github.finley243.adventureengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.quest.Quest;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.item.LootTable;
import com.github.finley243.adventureengine.world.object.LinkedObject;
import com.github.finley243.adventureengine.world.template.StatsActor;
import com.github.finley243.adventureengine.world.template.StatsItem;

public class Data {
	
	private static ActorPlayer player = null;
	
	private static Map<String, String> config = new HashMap<String, String>();
	
	private static Map<String, Area> areas = new HashMap<String, Area>();
	private static Map<String, Room> rooms = new HashMap<String, Room>();
	private static Map<String, Actor> actors = new HashMap<String, Actor>();
	private static Map<String, StatsActor> actorStats = new HashMap<String, StatsActor>();
	private static Map<String, LinkedObject> linkedObjects = new HashMap<String, LinkedObject>();
	private static Map<String, StatsItem> items = new HashMap<String, StatsItem>();
	private static Map<String, LootTable> lootTables = new HashMap<String, LootTable>();
	private static Map<String, DialogueTopic> topics = new HashMap<String, DialogueTopic>();
	private static Set<String> knowledge = new HashSet<String>();
	private static Map<String, Faction> factions = new HashMap<String, Faction>();
	private static List<Scene> scenes = new ArrayList<Scene>();
	private static Map<String, Quest> quests = new HashMap<String, Quest>();
	
	public static void addConfig(String id, String value) {
		if(config.containsKey(id)) {
			System.out.println("WARNING - Adding config with existing ID: " + id);
		}
		config.put(id, value);
	}
	
	public static String getConfig(String id) {
		return config.get(id);
	}
	
	public static void addArea(String id, Area value) {
		if(areas.containsKey(id)) {
			System.out.println("WARNING - Adding area with existing ID: " + id);
		}
		areas.put(id, value);
	}
	
	public static Area getArea(String id) {
		return areas.get(id);
	}
	
	public static void addRoom(String id, Room value) {
		if(rooms.containsKey(id)) {
			System.out.println("WARNING - Adding room with existing ID: " + id);
		}
		rooms.put(id, value);
	}
	
	public static Room getRoom(String id) {
		return rooms.get(id);
	}
	
	public static void addActor(String id, Actor value) {
		if(actors.containsKey(id)) {
			System.out.println("WARNING - Adding actor with existing ID: " + id);
		}
		actors.put(id, value);
	}
	
	public static Actor getActor(String id) {
		return actors.get(id);
	}
	
	public static Collection<Actor> getActors() {
		return actors.values();
	}
	
	public static void replaceActor(String id, Actor value) {
		if(!actors.containsKey(id)) {
			System.out.println("WARNING - Replacing actor with ID that does not exist: " + id);
		}
	}
	
	public static ActorPlayer getPlayer() {
		if(player == null) {
			player = (ActorPlayer) getActor(getConfig("playerID"));
		}
		return player;
	}
	
	public static void addActorStats(String id, StatsActor value) {
		if(actors.containsKey(id)) {
			System.out.println("WARNING - Adding actor stats with existing ID: " + id);
		}
		actorStats.put(id, value);
	}
	
	public static StatsActor getActorStats(String id) {
		return actorStats.get(id);
	}
	
	public static void addLinkedObject(String id, LinkedObject value) {
		if(linkedObjects.containsKey(id)) {
			System.out.println("WARNING - Adding linked object with existing ID: " + id);
		}
		linkedObjects.put(id, value);
	}
	
	public static LinkedObject getLinkedObject(String id) {
		return linkedObjects.get(id);
	}
	
	public static void addItem(String id, StatsItem value) {
		if(items.containsKey(id)) {
			System.out.println("WARNING - Adding item with existing ID: " + id);
		}
		items.put(id, value);
	}
	
	public static StatsItem getItem(String id) {
		return items.get(id);
	}
	
	public static void addLootTable(String id, LootTable value) {
		if(lootTables.containsKey(id)) {
			System.out.println("WARNING - Adding loot table with existing ID: " + id);
		}
		lootTables.put(id, value);
	}
	
	public static LootTable getLootTable(String id) {
		return lootTables.get(id);
	}
	
	public static void addTopic(String id, DialogueTopic value) {
		if(topics.containsKey(id)) {
			System.out.println("WARNING - Adding topic with existing ID: " + id);
		}
		topics.put(id, value);
	}
	
	public static DialogueTopic getTopic(String id) {
		return topics.get(id);
	}
	
	public static void addKnowledge(String value) {
		knowledge.add(value);
	}
	
	public static boolean hasKnowledge(String value) {
		return knowledge.contains(value);
	}
	
	public static void addFaction(String id, Faction value) {
		if(factions.containsKey(id)) {
			System.out.println("WARNING - Adding faction with existing ID: " + id);
		}
		factions.put(id, value);
	}
	
	public static Faction getFaction(String id) {
		return factions.get(id);
	}
	
	public static void addScene(Scene value) {
		scenes.add(value);
	}
	
	public static List<Scene> getScenes() {
		return scenes;
	}
	
	public static void addQuest(String id, Quest value) {
		if(quests.containsKey(id)) {
			System.out.println("WARNING - Adding quest with existing ID: " + id);
		}
		quests.put(id, value);
	}
	
	public static Quest getQuest(String id) {
		return quests.get(id);
	}
	
}
