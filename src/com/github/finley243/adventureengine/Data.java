package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.DataLoader;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.network.Network;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.StatParameters;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.LinkType;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
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

	private final Map<String, StatParameters> statParameters = new HashMap<>();
	private final Map<String, Area> areas = new HashMap<>();
	private final Map<String, Room> rooms = new HashMap<>();
	private final Map<String, Actor> actors = new HashMap<>();
	private final Map<String, ActorTemplate> actorStats = new HashMap<>();
	private final Map<String, ObjectTemplate> objectTemplates = new HashMap<>();
	private final Map<String, WorldObject> objects = new HashMap<>();
	private final Map<String, ItemTemplate> itemTemplates = new HashMap<>();
	private final Map<String, Item> itemStates = new HashMap<>();
	private final Map<String, LootTable> lootTables = new HashMap<>();
	private final Map<String, WeaponClass> weaponClasses = new HashMap<>();
	private final Map<String, WeaponAttackType> attackTypes = new HashMap<>();
	private final Map<String, Scene> scenes = new HashMap<>();
	private final Map<String, Script> scripts = new HashMap<>();
	private final Map<String, Expression> expressions = new HashMap<>();
	private final Map<String, Faction> factions = new HashMap<>();
	private final Map<String, Network> networks = new HashMap<>();
	private final Map<String, Timer> timers = new HashMap<>();
	private final Map<String, Effect> effects = new HashMap<>();
	private final Map<String, ActionTemplate> actionTemplates = new HashMap<>();
	private final Map<String, LinkType> linkTypes = new HashMap<>();

	private final Map<String, DamageType> damageTypes = new HashMap<>();
	private final Map<String, Attribute> attributes = new HashMap<>();
	private final Map<String, Skill> skills = new HashMap<>();

	private final Map<String, Boolean> globalBooleans = new HashMap<>();
	private final Map<String, Integer> globalIntegers = new HashMap<>();
	private final Map<String, Float> globalFloats = new HashMap<>();
	private final Map<String, String> globalStrings = new HashMap<>();
	private final Map<String, Set<String>> globalStringSets = new HashMap<>();

	public Data(Game game) {
		this.game = game;
		this.time = new DateTimeController();
	}

	public DateTimeController dateTime() {
		return time;
	}

	public void newGame() throws ParserConfigurationException, IOException, SAXException, GameDataException {
		reset();
		for (Area area : areas.values()) {
			area.onNewGameInit();
		}
		for (Actor actor : actors.values()) {
			actor.onNewGameInit();
		}
		for (WorldObject object : new ArrayList<>(objects.values())) {
			object.onNewGameInit();
		}
	}

	public void reset() throws ParserConfigurationException, IOException, SAXException, GameDataException {
		time.reset(this);
		statParameters.clear();
		areas.clear();
		rooms.clear();
		actors.clear();
		actorStats.clear();
		objectTemplates.clear();
		objects.clear();
		itemTemplates.clear();
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
		actionTemplates.clear();
		linkTypes.clear();
		damageTypes.clear();
		attributes.clear();
		skills.clear();
		globalIntegers.clear();
		globalFloats.clear();
		globalBooleans.clear();
		globalStrings.clear();
		globalStringSets.clear();
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
		for(ItemTemplate itemTemplate : itemTemplates.values()) {
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
		state.addAll(dateTime().saveState());
		return state;
	}

	public void loadState(List<SaveData> state) throws ParserConfigurationException, IOException, SAXException, GameDataException {
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

	public void addStatParameters(String id, StatParameters value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add stat parameters with blank ID");
		if(statParameters.containsKey(id)) throw new IllegalArgumentException("Cannot add stat parameters with existing ID: " + id);
		statParameters.put(id, value);
	}

	public StatParameters getStatParameters(String id) {
		return statParameters.get(id);
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
	
	public void addItemTemplate(String id, ItemTemplate value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add item with blank ID");
		if(itemTemplates.containsKey(id)) throw new IllegalArgumentException("Cannot add item with existing ID: " + id);
		itemTemplates.put(id, value);
	}
	
	public ItemTemplate getItemTemplate(String id) {
		return itemTemplates.get(id);
	}

	public void addItemState(String id, Item value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add item state with blank ID");
		if(itemStates.containsKey(id)) throw new IllegalArgumentException("Cannot add item state with existing ID: " + id);
		if(!value.hasState()) throw new UnsupportedOperationException("Cannot add item state for stateless item: " + value.getTemplateID());
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

	public void addScript(String id, Script value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add script with blank ID");
		if(scripts.containsKey(id)) throw new IllegalArgumentException("Cannot add script with existing ID: " + id);
		scripts.put(id, value);
	}

	public Script getScript(String id) {
		return scripts.get(id);
	}

	public void addExpression(String id, Expression value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add expression with blank ID");
		if(expressions.containsKey(id)) throw new IllegalArgumentException("Cannot add expression with existing ID: " + id);
		expressions.put(id, value);
	}

	public Expression getExpression(String id) {
		return expressions.get(id);
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

	public void addActionTemplate(String id, ActionTemplate value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add action template with blank ID");
		if(actionTemplates.containsKey(id)) throw new IllegalArgumentException("Cannot add action template with existing ID: " + id);
		actionTemplates.put(id, value);
	}

	public ActionTemplate getActionTemplate(String id) {
		return actionTemplates.get(id);
	}

	public void addLinkType(String id, LinkType value) {
		if(id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add link type with blank ID");
		if(actionTemplates.containsKey(id)) throw new IllegalArgumentException("Cannot add link type with existing ID: " + id);
		linkTypes.put(id, value);
	}

	public LinkType getLinkType(String id) {
		return linkTypes.get(id);
	}

	public void addDamageType(String id, DamageType value) {
		if (id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add damage type with blank ID");
		if (damageTypes.containsKey(id)) throw new IllegalArgumentException("Cannot add damage type with existing ID: " + id);
		damageTypes.put(id, value);
	}

	public DamageType getDamageType(String id) {
		return damageTypes.get(id);
	}

	public Set<String> getDamageTypeIDs() {
		return damageTypes.keySet();
	}

	public void addAttribute(String id, Attribute value) {
		if (id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add attribute with blank ID");
		if (attributes.containsKey(id)) throw new IllegalArgumentException("Cannot add attribute with existing ID: " + id);
		attributes.put(id, value);
	}

	public Attribute getAttribute(String id) {
		return attributes.get(id);
	}

	public Set<String> getAttributeIDs() {
		return attributes.keySet();
	}

	public void addSkill(String id, Skill value) {
		if (id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add skill with blank ID");
		if (skills.containsKey(id)) throw new IllegalArgumentException("Cannot add skill with existing ID: " + id);
		skills.put(id, value);
	}

	public Skill getSkill(String id) {
		return skills.get(id);
	}

	public Set<String> getSkillIDs() {
		return skills.keySet();
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
