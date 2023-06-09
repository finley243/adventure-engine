package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionInspectArea;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.*;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area extends GameInstanced implements Noun, MutableStatHolder {

	public enum AreaNameType {
		IN, ON, NEAR, FRONT, SIDE, BEHIND
	}

	private final String landmarkID;
	private final String name;
	private final AreaNameType nameType;
	private boolean isKnown;

	// The room containing this area
	private final String roomID;
	
	private final Scene description;

	private final String ownerFaction;
	// Whether members and allies of ownerFaction should react negatively to non-allies in area
	private final boolean isPrivate;
	
	// All areas that can be accessed when in this area (key is areaID)
	private final Map<String, AreaLink> linkedAreas;

	private final Map<String, Script> scripts;
	
	// All objects in this area
	private final Set<WorldObject> objects;
	// All actors in this area
	private final Set<Actor> actors;
	// Inventory containing all items in the area (that are not in object or actor inventories)
	private final Inventory itemInventory;

	private final StatStringSet effects;
	
	public Area(Game game, String ID, String landmarkID, String name, AreaNameType nameType, Scene description, String roomID, String ownerFaction, boolean isPrivate, Map<String, AreaLink> linkedAreas, Map<String, Script> scripts) {
		super(game, ID);
		if (landmarkID == null && name == null) throw new IllegalArgumentException("Landmark and name cannot both be null: " + ID);
		this.landmarkID = landmarkID;
		this.name = name;
		this.nameType = nameType;
		this.description = description;
		this.roomID = roomID;
		this.ownerFaction = ownerFaction;
		this.isPrivate = isPrivate;
		this.linkedAreas = linkedAreas;
		this.objects = new HashSet<>();
		this.actors = new HashSet<>();
		this.itemInventory = new Inventory(game, null);
		this.scripts = scripts;
		this.effects = new StatStringSet("effects", this);
	}

	public WorldObject getLandmark() {
		return game().data().getObject(landmarkID);
	}

	@Override
	public String getName() {
		if (landmarkID != null) {
			return getLandmark().getName();
		} else {
			return name;
		}
	}
	
	public Scene getDescription() {
		return description;
	}

	public String getOwnerFaction() {
		if (ownerFaction == null) return getRoom().getOwnerFaction();
		return ownerFaction;
	}

	public boolean isPrivate() {
		return isPrivate;
	}
	
	@Override
	public String getFormattedName() {
		if (landmarkID != null) {
			return getLandmark().getFormattedName();
		} else {
			return LangUtils.addArticle(name, !isKnown);
		}
	}

	public void setKnown() {
		if (landmarkID != null) {
			getLandmark().setKnown();
		} else {
			isKnown = true;
		}
	}

	@Override
	public boolean isKnown() {
		if (landmarkID != null) {
			return getLandmark().isKnown();
		} else {
			return isKnown;
		}
	}

	public String getRelativeName() {
		if (landmarkID != null) {
			return "near " + getLandmark().getFormattedName();
		} else {
			return switch (nameType) {
				case IN -> "in " + getFormattedName();
				case ON -> "on " + getFormattedName();
				case NEAR -> "near " + getFormattedName();
				case FRONT -> "in front of " + getFormattedName();
				case SIDE -> "beside " + getFormattedName();
				case BEHIND -> "behind " + getFormattedName();
			};
		}
	}

	public AreaLink.CompassDirection getRelativeDirection(Area origin) {
		if (origin.linkedAreas.containsKey(this.getID())) {
			return origin.linkedAreas.get(this.getID()).getDirection();
		}
		return null;
	}

	public String getMovePhrase() {
		if (landmarkID != null) {
			return "moveToward";
		} else {
			return switch (nameType) {
				case IN -> "moveTo";
				case ON -> "moveOnto";
				case FRONT -> "moveFront";
				case BEHIND -> "moveBehind";
				case SIDE -> "moveBeside";
				case NEAR -> "moveToward";
			};
		}
	}

	public void onNewGameInit() {

	}
	
	public Set<WorldObject> getObjects(){
		return objects;
	}
	
	public void addObject(WorldObject object) {
		boolean didAdd = objects.add(object);
		if (!didAdd) {
			System.out.println("Area " + getID() + " already contains object " + object + ".");
		}
	}
	
	public void removeObject(WorldObject object) {
		boolean didRemove = objects.remove(object);
		if (!didRemove) {
			System.out.println("Area " + getID() + " does not contain object " + object + ".");
		}
	}
	
	public Set<Actor> getActors(){
		return actors;
	}
	
	public void addActor(Actor actor) {
		boolean didAdd = actors.add(actor);
		if (!didAdd) {
			System.out.println("Area " + getID() + " already contains actor " + actor + ".");
		}
	}
	
	public void removeActor(Actor actor) {
		boolean didRemove = actors.remove(actor);
		if (!didRemove) {
			System.out.println("Area " + getID() + " does not contain actor " + actor + ".");
		}
	}

	public Set<AttackTarget> getAttackTargets() {
		Set<AttackTarget> targets = new HashSet<>(getActors());
		targets.addAll(getObjects());
		return targets;
	}

	public Inventory getInventory() {
		return itemInventory;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		if ("room".equals(name)) {
			return getRoom();
		}
		return null;
	}

	public List<Action> getItemActions() {
		return itemInventory.getAreaActions(this);
	}

	public List<Action> getMoveActions(String vehicleType, WorldObject vehicleObject, String menuCategory) {
		List<Action> moveActions = new ArrayList<>();
		for (AreaLink link : linkedAreas.values()) {
			if (vehicleType != null && link.isVehicleMovable(game(), vehicleType) || vehicleType == null && link.isMovable(game())) {
				String actionTemplate = vehicleType == null ? game().data().getLinkType(link.getType()).getActorMoveAction() : game().data().getLinkType(link.getType()).getVehicleMoveAction(vehicleType);
				moveActions.add(new ActionCustom(game(), vehicleObject, actionTemplate, new MapBuilder<String, Expression>().put("areaID", new ExpressionConstantString(link.getAreaID())).put("dir", new ExpressionConstantString(link.getDirection().toString())).build(), new String[] {menuCategory}, true));
			}
		}
		return moveActions;
	}

	public Set<Area> getMovableAreas(String vehicleType) {
		Set<Area> movableAreas = new HashSet<>();
		for (AreaLink link : linkedAreas.values()) {
			if (vehicleType != null && link.isVehicleMovable(game(), vehicleType) || vehicleType == null && link.isMovable(game())) {
				movableAreas.add(game().data().getArea(link.getAreaID()));
			}
		}
		return movableAreas;
	}

	private Set<String> getMovableAreaIDs(String vehicleType) {
		Set<String> movableAreas = new HashSet<>();
		for (AreaLink link : linkedAreas.values()) {
			if (vehicleType != null && link.isVehicleMovable(game(), vehicleType) || vehicleType == null && link.isMovable(game())) {
				movableAreas.add(link.getAreaID());
			}
		}
		return movableAreas;
	}

	public Set<Area> getLineOfSightAreas() {
		Set<Area> visibleAreas = new HashSet<>();
		visibleAreas.add(this);
		for (AreaLink link : linkedAreas.values()) {
			if (game().data().getLinkType(link.getType()).isVisible()) {
					Area area = game().data().getArea(link.getAreaID());
					visibleAreas.add(area);
			}
		}
		for (RoomLink roomLink : getRoom().getLinkedRooms().values()) {
			visibleAreas.addAll(game().data().getRoom(roomLink.getRoomID()).getAreas());
		}
		return visibleAreas;
	}

	public Set<String> getLineOfSightAreaIDs() {
		Set<String> visibleAreaIDs = new HashSet<>();
		visibleAreaIDs.add(this.getID());
		for (AreaLink link : linkedAreas.values()) {
			if (game().data().getLinkType(link.getType()).isVisible()) {
				visibleAreaIDs.add(link.getAreaID());
			}
		}
		for (RoomLink roomLink : getRoom().getLinkedRooms().values()) {
			visibleAreaIDs.addAll(game().data().getRoom(roomLink.getRoomID()).getAreaIDs());
		}
		return visibleAreaIDs;
	}

	public boolean isVisible(Actor subject, String areaID) {
		if (linkedAreas.containsKey(areaID)) {
			AreaLink link = linkedAreas.get(areaID);
			return game().data().getLinkType(link.getType()).isVisible();
		} else {
			return getRoom().getLinkedRooms().containsKey(game().data().getArea(areaID).getRoom().getID());
		}
	}

	public AreaLink.DistanceCategory getDistanceTo(String areaID) {
		if (this.getID().equals(areaID)) return AreaLink.DistanceCategory.NEAR;
		if (!linkedAreas.containsKey(areaID)) return null;
		return linkedAreas.get(areaID).getDistance();
	}

	public Set<Area> visibleAreasInRange(Actor subject, Set<AreaLink.DistanceCategory> ranges) {
		Set<Area> areas = new HashSet<>();
		if (ranges.contains(AreaLink.DistanceCategory.NEAR)) {
			areas.add(this);
			if (ranges.size() == 1) {
				return areas;
			}
		}
		for (AreaLink link : linkedAreas.values()) {
			if (isVisible(subject, link.getAreaID()) && ranges.contains(link.getDistance())) {
				areas.add(game().data().getArea(link.getAreaID()));
			}
		}
		for (RoomLink roomLink : getRoom().getLinkedRooms().values()) {
			if (ranges.contains(roomLink.getDistance())) {
				areas.addAll(game().data().getRoom(roomLink.getRoomID()).getAreas());
			}
		}
		return areas;
	}

	public void onStartRound() {
		applyEffects();
	}

	public void applyEffects() {
		for (Actor actor : getActors()) {
			for (String effectID : effects.value(new HashSet<>())) {
				actor.getEffectComponent().addEffect(effectID);
			}
		}
	}

	@Override
	public boolean isProperName() {
		return getLandmark().isProperName();
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}

	@Override
	public boolean forcePronoun() {
		return false;
	}
	
	public Room getRoom() {
		return game().data().getRoom(roomID);
	}

	public List<Action> getAreaActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionInspectArea(this));
		return actions;
	}

	@Override
	public int getValueInt(String name) {
		return 0;
	}

	@Override
	public float getValueFloat(String name) {
		return 0;
	}

	@Override
	public boolean getValueBoolean(String name) {
		if ("known".equals(name)) {
			return isKnown;
		}
		return false;
	}

	@Override
	public String getValueString(String name) {
		return switch (name) {
			case "id" -> getID();
			case "name" -> getName();
			case "relative_name" -> getRelativeName();
			case "move_phrase" -> getMovePhrase();
			case "room" -> roomID;
			default -> null;
		};
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return switch (name) {
			case "visible_areas" -> getLineOfSightAreaIDs();
			case "movable_areas" -> getMovableAreaIDs(null);
			default -> null;
		};
	}

	@Override
	public void setStateBoolean(String name, boolean value) {
		if ("known".equals(name)) {
			isKnown = value;
		}
	}

	@Override
	public void setStateInteger(String name, int value) {

	}

	@Override
	public void setStateFloat(String name, float value) {

	}

	@Override
	public void setStateString(String name, String value) {

	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {

	}

	@Override
	public void modStateInteger(String name, int amount) {

	}

	@Override
	public void modStateFloat(String name, float amount) {

	}

	@Override
	public StatInt getStatInt(String name) {
		return null;
	}

	@Override
	public StatFloat getStatFloat(String name) {
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		return null;
	}

	@Override
	public StatString getStatString(String name) {
		return null;
	}

	@Override
	public StatStringSet getStatStringSet(String name) {
		if ("effects".equals(name)) {
			return effects;
		}
		return null;
	}

	@Override
	public void onStatChange(String name) {
		if ("effects".equals(name)) {
			applyEffects();
		}
	}

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if (scripts.containsKey(entryPoint)) {
			scripts.get(entryPoint).execute(new Context(game(), subject, target));
		}
	}

	public void loadState(SaveData saveData) {
		if (saveData.getParameter().equals("is_known")) {
			if (saveData.getValueBoolean()) {
				setKnown();
			}
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (isKnown()) {
			state.add(new SaveData(SaveData.DataType.AREA, this.getID(), "is_known", isKnown()));
		}
		return state;
	}
	
}
