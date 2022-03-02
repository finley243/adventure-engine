package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.network.Network;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.item.LootTable;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.template.StatsActor;
import com.github.finley243.adventureengine.world.template.StatsItem;

import java.util.*;

public class Data {

	private static final String PLAYER_ID_CONFIG = "playerID";

	private ActorPlayer player;
	
	private final Map<String, String> config = new HashMap<>();
	
	private final Map<String, Area> areas = new HashMap<>();
	private final Map<String, Room> rooms = new HashMap<>();
	private final Map<String, Actor> actors = new HashMap<>();
	private final Map<String, StatsActor> actorStats = new HashMap<>();
	private final Map<String, WorldObject> objects = new HashMap<>();
	private final Map<String, StatsItem> items = new HashMap<>();
	private final Map<String, LootTable> lootTables = new HashMap<>();
	private final Map<String, DialogueTopic> topics = new HashMap<>();
	private final Map<String, Integer> variables = new HashMap<>();
	private final Map<String, Faction> factions = new HashMap<>();
	private final Map<String, Scene> scenes = new HashMap<>();
	private final Map<String, Network> networks = new HashMap<>();

	public Data() {}
	
	public void addConfig(String id, String value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add config with blank ID");
		if(config.containsKey(id)) throw new IllegalArgumentException("Cannot add config with existing ID: " + id);
		config.put(id, value);
	}
	
	public String getConfig(String id) {
		return config.get(id);
	}
	
	public void addArea(String id, Area value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add area with blank ID");
		if(areas.containsKey(id)) throw new IllegalArgumentException("Cannot add area with existing ID: " + id);
		areas.put(id, value);
	}
	
	public Area getArea(String id) {
		return areas.get(id);
	}
	
	public void addRoom(String id, Room value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add room with blank ID");
		if(rooms.containsKey(id)) throw new IllegalArgumentException("Cannot add room with existing ID: " + id);
		rooms.put(id, value);
	}
	
	public Room getRoom(String id) {
		return rooms.get(id);
	}
	
	public void addActor(String id, Actor value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add actor with blank ID");
		if(actors.containsKey(id)) throw new IllegalArgumentException("Cannot add actor with existing ID: " + id);
		actors.put(id, value);
	}
	
	public Actor getActor(String id) {
		return actors.get(id);
	}
	
	public Collection<Actor> getActors() {
		return actors.values();
	}
	
	public ActorPlayer getPlayer() {
		if(player == null) {
			player = (ActorPlayer) getActor(getConfig(PLAYER_ID_CONFIG));
		}
		return player;
	}
	
	public void addActorStats(String id, StatsActor value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add actor stats with blank ID");
		if(actorStats.containsKey(id)) throw new IllegalArgumentException("Cannot add actor stats with existing ID: " + id);
		actorStats.put(id, value);
	}
	
	public StatsActor getActorStats(String id) {
		return actorStats.get(id);
	}
	
	public void addObject(String id, WorldObject value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add object with blank ID");
		if(objects.containsKey(id)) throw new IllegalArgumentException("Cannot add object with existing ID: " + id);
		objects.put(id, value);
	}
	
	public WorldObject getObject(String id) {
		return objects.get(id);
	}
	
	public void addItem(String id, StatsItem value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add item with blank ID");
		if(items.containsKey(id)) throw new IllegalArgumentException("Cannot add item with existing ID: " + id);
		items.put(id, value);
	}
	
	public StatsItem getItem(String id) {
		return items.get(id);
	}
	
	public void addLootTable(String id, LootTable value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add loot table with blank ID");
		if(lootTables.containsKey(id)) throw new IllegalArgumentException("Cannot add loot table with existing ID: " + id);
		lootTables.put(id, value);
	}
	
	public LootTable getLootTable(String id) {
		return lootTables.get(id);
	}
	
	public void addTopic(String id, DialogueTopic value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add topic with blank ID");
		if(topics.containsKey(id)) throw new IllegalArgumentException("Cannot add topic with existing ID: " + id);
		topics.put(id, value);
	}
	
	public DialogueTopic getTopic(String id) {
		return topics.get(id);
	}

	public void setVariable(String id, int value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot set variable with blank ID");
		variables.put(id, value);
	}

	public int getVariable(String id) {
		if(!variables.containsKey(id)) return 0;
		return variables.get(id);
	}
	
	public void addFaction(String id, Faction value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add faction with blank ID");
		if(factions.containsKey(id)) throw new IllegalArgumentException("Cannot add faction with existing ID: " + id);
		factions.put(id, value);
	}
	
	public Faction getFaction(String id) {
		return factions.get(id);
	}
	
	public void addScene(String id, Scene value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add scene with blank ID");
		if(scenes.containsKey(id)) throw new IllegalArgumentException("Cannot add scene with existing ID: " + id);
		scenes.put(id, value);
	}

	public Scene getScene(String id) {
		return scenes.get(id);
	}
	
	public Collection<Scene> getScenes() {
		return scenes.values();
	}

	public void addNetwork(String id, Network value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add network with blank ID");
		if(networks.containsKey(id)) throw new IllegalArgumentException("Cannot add network with existing ID: " + id);
		networks.put(id, value);
	}

	public Network getNetwork(String id) {
		return networks.get(id);
	}
	
}
