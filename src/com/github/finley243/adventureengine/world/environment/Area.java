package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.*;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionInspectArea;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.menu.action.MenuDataMove;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;

import java.util.*;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area extends GameInstanced implements Noun, MutableStatHolder {

	public enum RestrictionType {
		PUBLIC, PRIVATE, HOSTILE
	}

	public enum AreaNameType {
		IN, ON, NEAR, FRONT, SIDE, BEHIND
	}

	private final String landmarkID;
	private final String name;
	private final AreaNameType nameType;
	private final boolean nameIsPlural;
	private boolean isKnown;

	// The room containing this area
	private final String roomID;
	
	private final Scene description;

	private final String ownerFaction;
	private final RestrictionType restrictionType;
	private final Boolean allowAllies;
	
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
	
	public Area(Game game, String ID, String landmarkID, String name, AreaNameType nameType, boolean nameIsPlural, Scene description, String roomID, String ownerFaction, RestrictionType restrictionType, Boolean allowAllies, Map<String, AreaLink> linkedAreas, Map<String, Script> scripts) {
		super(game, ID);
		this.landmarkID = landmarkID;
		this.name = name;
		this.nameType = nameType;
		this.nameIsPlural = nameIsPlural;
		this.description = description;
		this.roomID = roomID;
		this.ownerFaction = ownerFaction;
		this.restrictionType = restrictionType;
		this.allowAllies = allowAllies;
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
		} else if (name != null) {
			return name;
		} else {
			return getRoom().getName();
		}
	}
	
	public Scene getDescription() {
		return description;
	}

	public String getOwnerFaction() {
		if (ownerFaction == null) return getRoom().getOwnerFaction();
		return ownerFaction;
	}

	public RestrictionType getRestrictionType() {
		if (restrictionType == null) return getRoom().getRestrictionType();
		return restrictionType;
	}

	public boolean allowAllies() {
		if (allowAllies == null) return getRoom().allowAllies();
		return allowAllies;
	}

	@Override
	public void setKnown() {
		if (landmarkID != null) {
			getLandmark().setKnown();
		} else if (name == null) {
			getRoom().setKnown();
		}
		isKnown = true;
	}

	@Override
	public boolean isKnown() {
		if (landmarkID != null) {
			return getLandmark().isKnown();
		} else if (name == null) {
			return getRoom().isKnown();
		} else {
			return isKnown;
		}
	}

	public String getRelativeName() {
		if (name == null && landmarkID == null) {
			return getRoom().getRelativeName();
		}
		return switch (nameType) {
			case IN -> "in";
			case ON -> "on";
			case NEAR -> "near";
			case FRONT -> "in front of";
			case SIDE -> "beside";
			case BEHIND -> "behind";
		};
	}

	public String getMovePhrase(Actor subject) {
		if (name == null && landmarkID == null) {
			return getRoom().getMovePhrase(subject);
		}
		return switch (nameType) {
			case IN -> "moveTo";
			case ON -> "moveOnto";
			case FRONT -> "moveFront";
			case BEHIND -> "moveBehind";
			case SIDE -> "moveBeside";
			case NEAR -> "moveToward";
		};
	}

	public void onInit() {}
	
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

	public List<Action> getMoveActions(Actor subject, String vehicleType, WorldObject vehicleObject) {
		List<Action> moveActions = new ArrayList<>();
		for (AreaLink link : linkedAreas.values()) {
			if (vehicleType != null && link.isVehicleMovable(game(), vehicleType) || vehicleType == null && link.isMovable(game())) {
				String actionTemplate = vehicleType == null ? game().data().getLinkType(link.getType()).getActorMoveAction() : game().data().getLinkType(link.getType()).getVehicleMoveAction(vehicleType);
				moveActions.add(new ActionCustom(game(), null, vehicleObject, null, game().data().getArea(link.getAreaID()), actionTemplate, new MapBuilder<String, Script>().put("dir", Script.constant(link.getDirection().toString())).put("dir_name", Script.constant(link.getDirection().name)).build(), new MenuDataMove(game().data().getArea(link.getAreaID()), link.getDirection()), true));
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

	public boolean isVisible(Actor subject) {
		return true;
	}

	public boolean hasLineOfSightObstruction() {
		return false;
	}

	public ObstructionData getLineOfSightObstruction() {
		return null;
	}

	public Set<AreaLink> getDirectVisibleLinkedAreas() {
		Set<AreaLink> visibleAreas = new HashSet<>();
		for (AreaLink link : linkedAreas.values()) {
			// TODO - Evaluate whether this check is necessary (does not allow non-visible paths to be calculated for reachability)
			if (game().data().getLinkType(link.getType()).isVisible()) {
				//Area area = game().data().getArea(link.getAreaID());
				visibleAreas.add(link);
			}
		}
		/*for (WorldObject object : getObjects()) {
			ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
			if (linkComponent == null) continue;
			visibleAreas.addAll(linkComponent.getLinkedLineOfSightAreas());
		}*/
		return visibleAreas;
	}

	public Map<WorldObject, Set<Area>> getObjectVisibleLinkedAreas() {
		Map<WorldObject, Set<Area>> visibleAreas = new HashMap<>();
		for (WorldObject object : getObjects()) {
			ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
			if (linkComponent == null) continue;
			visibleAreas.put(object, linkComponent.getLinkedLineOfSightAreas());
		}
		return visibleAreas;
	}

	public boolean hasDirectVisibleLinkTo(Area area) {
		if (linkedAreas.containsKey(area.getID())) {
			AreaLink link = linkedAreas.get(area.getID());
			return game().data().getLinkType(link.getType()).isVisible();
		}
		for (WorldObject object : getObjects()) {
			ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
			if (linkComponent == null) continue;
			if (linkComponent.getLinkedLineOfSightAreas().contains(area)) {
				return true;
			}
		}
		return false;
	}

	public AreaLink.CompassDirection getLinkDirectionTo(Area area) {
		if (linkedAreas.containsKey(area.getID())) {
			AreaLink link = linkedAreas.get(area.getID());
			return link.getDirection();
		}
		for (WorldObject object : getObjects()) {
			ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
			if (linkComponent == null) continue;
			Map<Area, AreaLink.CompassDirection> visibleAreasWithDirections = linkComponent.getLinkedLineOfSightAreasWithDirections();
			if (visibleAreasWithDirections.containsKey(area)) {
				return visibleAreasWithDirections.get(area);
			}
		}
		return null;
	}

	public AreaLink.DistanceCategory getLinearDistanceTo(Area area) {
		if (linkedAreas.containsKey(area.getID())) {
			return linkedAreas.get(area.getID()).getDistance();
		}
		Pathfinder.VisibleAreaData areaData = Pathfinder.getLineOfSightAreas(this).get(area);
		if (areaData == null) return null;
		return areaData.distance();
	}

	public Set<Area> visibleAreasInRange(Actor subject, Set<AreaLink.DistanceCategory> ranges) {
		Set<Area> areas = new HashSet<>();
		Map<Area, Pathfinder.VisibleAreaData> visibleAreas = subject.getVisibleAreas();
		for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : visibleAreas.entrySet()) {
			if (ranges.contains(entry.getValue().distance())) {
				areas.add(entry.getKey());
			}
		}
		return areas;
	}

	public void onStartRound() {
		applyEffects();
	}

	public void applyEffects() {
		for (Actor actor : getActors()) {
			for (String effectID : effects.value(new HashSet<>(), new Context(game(), actor, actor))) {
				actor.getEffectComponent().addEffect(effectID);
			}
		}
	}

	@Override
	public boolean isProperName() {
		if (landmarkID != null) {
			return getLandmark().isProperName();
		} else if (name == null) {
			return getRoom().isProperName();
		} else {
			return false;
		}
	}

	@Override
	public int pluralCount() {
		if (nameIsPlural) {
			return 3;
		}
		return 1;
	}

	@Override
	public Pronoun getPronoun() {
		if (nameIsPlural) {
			return Pronoun.THEY;
		}
		return Pronoun.IT;
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
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "inventory" -> (itemInventory == null ? null : Expression.constant(itemInventory));
			case "noun" -> Expression.constant((Noun) this);
			case "id" -> Expression.constant(getID());
			case "name" -> Expression.constant(getName());
			case "relative_name" -> Expression.constant(getRelativeName());
			case "move_phrase" -> Expression.constant(getMovePhrase(context.getSubject()));
			case "room" -> Expression.constant(roomID);
			//case "visible_areas" -> new ExpressionConstantStringSet(getLineOfSightAreaIDs());
			case "movable_areas" -> Expression.constant(getMovableAreaIDs(null));
			default -> null;
		};
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		return false;
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
			Context context = new Context(game(), subject, target);
			scripts.get(entryPoint).execute(context);
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

	public static AreaLink.DistanceCategory pathLengthToDistance(int pathLength) {
		for (AreaLink.DistanceCategory distance : AreaLink.DistanceCategory.values()) {
			if (pathLength >= distance.minPathLength && (distance.maxPathLength == -1 || pathLength <= distance.maxPathLength)) {
				return distance;
			}
		}
		return null;
	}

	public record ObstructionData(float hitChanceModifier, float detectionChanceModifier, boolean isTotalObstruction) {}
	
}
