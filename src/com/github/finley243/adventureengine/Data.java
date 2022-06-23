package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.load.DataLoader;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.network.Network;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.world.object.ObjectContainer;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Data {

	private Actor player;

	private final Game game;
	private final DateTimeController time;
	
	private final Map<String, String> config = new HashMap<>();
	
	private final Map<String, Area> areas = new HashMap<>();
	private final Map<String, Room> rooms = new HashMap<>();
	private final Map<String, Actor> actors = new HashMap<>();
	private final Map<String, ActorTemplate> actorStats = new HashMap<>();
	private final Map<String, WorldObject> objects = new HashMap<>();
	private final Map<String, ItemTemplate> items = new HashMap<>();
	private final Map<String, Item> itemStates = new HashMap<>();
	private final Map<String, LootTable> lootTables = new HashMap<>();
	private final Map<String, DialogueTopic> topics = new HashMap<>();
	private final Map<String, Integer> variables = new HashMap<>();
	private final Map<String, Script> scripts = new HashMap<>();
	private final Map<String, Faction> factions = new HashMap<>();
	private final Map<String, Scene> scenes = new HashMap<>();
	private final Map<String, Network> networks = new HashMap<>();

	public Data(Game game) {
		this.game = game;
		this.time = new DateTimeController();
	}

	public DateTimeController time() {
		return time;
	}

	public void newGame() throws ParserConfigurationException, IOException, SAXException {
		reset();
		for(Actor actor : actors.values()) {
			actor.newGameInit();
		}
		// Using ArrayList to avoid Concurrent Modification Exception
		for(WorldObject object : new ArrayList<>(objects.values())) {
			if (object instanceof ObjectContainer) {
				((ObjectContainer) object).newGameInit();
			}
		}
	}

	public void reset() throws ParserConfigurationException, IOException, SAXException {
		time.reset(this);
		areas.clear();
		rooms.clear();
		actors.clear();
		actorStats.clear();
		objects.clear();
		items.clear();
		itemStates.clear();
		lootTables.clear();
		topics.clear();
		variables.clear();
		scripts.clear();
		factions.clear();
		scenes.clear();
		networks.clear();
		DataLoader.loadFromDir(game, new File(Game.GAMEFILES + Game.DATA_DIRECTORY));
		player = ActorFactory.createPlayer(game, getConfig("playerID"), getArea(getConfig("playerStartArea")), getActorTemplate(getConfig("playerStats")));
		addActor(player.getID(), player);
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		for(Area area : areas.values()) {
			state.addAll(area.saveState());
		}
		for(Room room : rooms.values()) {
			state.addAll(room.saveState());
		}
		for(Actor actor : actors.values()) {
			state.addAll(actor.saveState());
		}
		for(WorldObject object : objects.values()) {
			state.addAll(object.saveState());
		}
		for(ItemTemplate itemTemplate : items.values()) {
			state.addAll(itemTemplate.saveState());
		}
		for(DialogueTopic topic : topics.values()) {
			state.addAll(topic.saveState());
		}
		for(String variable : variables.keySet()) {
			if(variables.get(variable) != 0) {
				state.add(new SaveData(SaveData.DataType.VARIABLE, variable, null, variables.get(variable)));
			}
		}
		state.addAll(time().saveState());
		for(Scene scene : scenes.values()) {
			state.addAll(scene.saveState());
		}
		return state;
	}

	public void loadState(List<SaveData> state) throws ParserConfigurationException, IOException, SAXException {
		reset();
		// TODO - Improve efficiency
		List<SaveData> nonItemSaveData = new ArrayList<>();
		for(SaveData saveData : state) {
			if(saveData.isItemInstance()) {
				saveData.apply(this);
			} else {
				nonItemSaveData.add(saveData);
			}
		}
		for(SaveData saveData : nonItemSaveData) {
			saveData.apply(this);
		}
	}

	public Game game() {
		return game;
	}
	
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

	public Collection<Area> getAreas() {
		return areas.values();
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
	
	public Actor getPlayer() {
		return player;
	}
	
	public void addActorTemplate(String id, ActorTemplate value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add actor stats with blank ID");
		if(actorStats.containsKey(id)) throw new IllegalArgumentException("Cannot add actor stats with existing ID: " + id);
		actorStats.put(id, value);
	}
	
	public ActorTemplate getActorTemplate(String id) {
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

	public void removeObject(String id) {
		if(!objects.containsKey(id)) throw new IllegalArgumentException("Cannot remove object because ID does not exist: " + id);
		objects.remove(id);
	}
	
	public void addItem(String id, ItemTemplate value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add item with blank ID");
		if(items.containsKey(id)) throw new IllegalArgumentException("Cannot add item with existing ID: " + id);
		items.put(id, value);
	}
	
	public ItemTemplate getItem(String id) {
		return items.get(id);
	}

	public void addItemState(String id, Item value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add item state with blank ID");
		if(itemStates.containsKey(id)) throw new IllegalArgumentException("Cannot add item state with existing ID: " + id);
		if(!value.getTemplate().hasState()) throw new UnsupportedOperationException("Cannot add item state for stateless item: " + value.getTemplate().getID());
		itemStates.put(id, value);
	}

	public Item getItemState(String id) {
		return itemStates.get(id);
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

	public void addScript(String id, Script script) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add script with blank ID");
		if(scripts.containsKey(id)) throw new IllegalArgumentException("Cannot add script with existing ID: " + id);
		scripts.put(id, script);
	}

	public Script getScript(String id) {
		return scripts.get(id);
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

	public void addNetwork(String id, Network value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add network with blank ID");
		if(networks.containsKey(id)) throw new IllegalArgumentException("Cannot add network with existing ID: " + id);
		networks.put(id, value);
	}

	public Network getNetwork(String id) {
		return networks.get(id);
	}
	
}
