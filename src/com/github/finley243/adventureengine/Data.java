package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorFactory;
import com.github.finley243.adventureengine.actor.ActorTemplate;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.load.DataLoader;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.network.Network;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;
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
	private final Map<String, ObjectTemplate> objectTemplates = new HashMap<>();
	private final Map<String, ObjectComponentTemplate> objectComponentTemplates = new HashMap<>();
	private final Map<String, WorldObject> objects = new HashMap<>();
	private final Map<String, ItemTemplate> items = new HashMap<>();
	private final Map<String, Item> itemStates = new HashMap<>();
	private final Map<String, LootTable> lootTables = new HashMap<>();
	private final Map<String, WeaponClass> weaponClasses = new HashMap<>();
	private final Map<String, WeaponAttackType> attackTypes = new HashMap<>();
	private final Map<String, Scene> scenes = new HashMap<>();
	private final Map<String, Script> scripts = new HashMap<>();
	private final Map<String, Faction> factions = new HashMap<>();
	private final Map<String, Network> networks = new HashMap<>();
	private final Map<String, Timer> timers = new HashMap<>();
	private final Map<String, Effect> effects = new HashMap<>();

	private final Map<String, Boolean> globalBooleans = new HashMap<>();
	private final Map<String, Integer> globalIntegers = new HashMap<>();
	private final Map<String, Float> globalFloats = new HashMap<>();
	private final Map<String, String> globalStrings = new HashMap<>();
	private final Map<String, Set<String>> globalStringSets = new HashMap<>();

	public Data(Game game) {
		this.game = game;
		this.time = new DateTimeController();
	}

	public DateTimeController time() {
		return time;
	}

	public void newGame() throws ParserConfigurationException, IOException, SAXException {
		reset();
		for (Area area : areas.values()) {
			area.onNewGameInit();
		}
		for (Actor actor : actors.values()) {
			actor.onNewGameInit();
		}
		// Using ArrayList to avoid Concurrent Modification Exception
		for (WorldObject object : new ArrayList<>(objects.values())) {
			object.onNewGameInit();
		}
	}

	public void reset() throws ParserConfigurationException, IOException, SAXException {
		time.reset(this);
		areas.clear();
		rooms.clear();
		actors.clear();
		actorStats.clear();
		objectTemplates.clear();
		objectComponentTemplates.clear();
		objects.clear();
		items.clear();
		itemStates.clear();
		lootTables.clear();
		weaponClasses.clear();
		attackTypes.clear();
		scenes.clear();
		scripts.clear();
		factions.clear();
		networks.clear();
		timers.clear();
		effects.clear();
		globalIntegers.clear();
		DataLoader.loadFromDir(game, new File(Game.GAMEFILES + Game.DATA_DIRECTORY));
		player = ActorFactory.createPlayer(game, getConfig("playerID"), getArea(getConfig("playerStartArea")), getConfig("playerStats"));
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
		for(Scene topic : scenes.values()) {
			state.addAll(topic.saveState());
		}
		for(String variable : globalIntegers.keySet()) {
			if(globalIntegers.get(variable) != 0) {
				state.add(new SaveData(SaveData.DataType.VARIABLE, variable, null, globalIntegers.get(variable)));
			}
		}
		state.addAll(time().saveState());
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

	public void addObjectTemplate(String id, ObjectTemplate value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add object template with blank ID");
		if(objectTemplates.containsKey(id)) throw new IllegalArgumentException("Cannot add object template with existing ID: " + id);
		objectTemplates.put(id, value);
	}

	public ObjectTemplate getObjectTemplate(String id) {
		return objectTemplates.get(id);
	}

	public void addObjectComponentTemplate(String id, ObjectComponentTemplate value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add object component template with blank ID");
		if(objectComponentTemplates.containsKey(id)) throw new IllegalArgumentException("Cannot add object component template with existing ID: " + id);
		objectComponentTemplates.put(id, value);
	}

	public ObjectComponentTemplate getObjectComponentTemplate(String id) {
		return objectComponentTemplates.get(id);
	}

	public void addObject(String id, WorldObject value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add object with blank ID");
		if(objects.containsKey(id)) throw new IllegalArgumentException("Cannot add object with existing ID: " + id);
		objects.put(id, value);
	}
	
	public WorldObject getObject(String id) {
		return objects.get(id);
	}

	public Collection<WorldObject> getObjects() {
		return objects.values();
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

	public void addWeaponClass(String id, WeaponClass weaponClass) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add weapon class with blank ID");
		if(weaponClasses.containsKey(id)) throw new IllegalArgumentException("Cannot add weapon class with existing ID: " + id);
		weaponClasses.put(id, weaponClass);
	}

	public WeaponClass getWeaponClass(String id) {
		return weaponClasses.get(id);
	}

	public void addAttackType(String id, WeaponAttackType attackType) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add attack type with blank ID");
		if(weaponClasses.containsKey(id)) throw new IllegalArgumentException("Cannot add attack type with existing ID: " + id);
		attackTypes.put(id, attackType);
	}

	public WeaponAttackType getAttackType(String id) {
		return attackTypes.get(id);
	}
	
	public void addScene(String id, Scene value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add scene with blank ID");
		if(scenes.containsKey(id)) throw new IllegalArgumentException("Cannot add scene with existing ID: " + id);
		scenes.put(id, value);
	}
	
	public Scene getScene(String id) {
		return scenes.get(id);
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

	public void addNetwork(String id, Network value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add network with blank ID");
		if(networks.containsKey(id)) throw new IllegalArgumentException("Cannot add network with existing ID: " + id);
		networks.put(id, value);
	}

	public Network getNetwork(String id) {
		return networks.get(id);
	}

	public void addTimer(String id, Timer value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add timer with blank ID");
		timers.put(id, value);
	}

	public void removeTimer(String id) {
		timers.remove(id);
	}

	public Timer getTimer(String id) {
		return timers.get(id);
	}

	public Collection<Timer> getTimers() {
		return timers.values();
	}

	public boolean isTimerActive(String id) {
		return timers.containsKey(id);
	}

	public void addEffect(String id, Effect value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add effect with blank ID");
		if(effects.containsKey(id)) throw new IllegalArgumentException("Cannot add effect with existing ID: " + id);
		effects.put(id, value);
	}

	public Effect getEffect(String id) {
		return effects.get(id);
	}

	public void setGlobalBoolean(String id, boolean value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot set global variable with blank ID");
		globalBooleans.put(id, value);
	}

	public boolean getGlobalBoolean(String id) {
		if(!globalBooleans.containsKey(id)) return false;
		return globalBooleans.get(id);
	}

	public void setGlobalInteger(String id, int value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot set global variable with blank ID");
		globalIntegers.put(id, value);
	}

	public int getGlobalInteger(String id) {
		if(!globalIntegers.containsKey(id)) return 0;
		return globalIntegers.get(id);
	}

	public void setGlobalFloat(String id, float value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot set global variable with blank ID");
		globalFloats.put(id, value);
	}

	public float getGlobalFloat(String id) {
		if(!globalFloats.containsKey(id)) return 0.0f;
		return globalFloats.get(id);
	}

	public void setGlobalString(String id, String value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot set global variable with blank ID");
		globalStrings.put(id, value);
	}

	public String getGlobalString(String id) {
		if(!globalStrings.containsKey(id)) return null;
		return globalStrings.get(id);
	}

	public void setGlobalStringSet(String id, Set<String> value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot set global variable with blank ID");
		globalStringSets.put(id, value);
	}

	public Set<String> getGlobalStringSet(String id) {
		if(!globalStringSets.containsKey(id)) return null;
		return globalStringSets.get(id);
	}
	
}
