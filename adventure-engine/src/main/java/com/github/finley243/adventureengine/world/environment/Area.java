package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.EffectComponent;
import com.github.finley243.adventureengine.effect.Effectable;
import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.AreaRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.menu.action.MenuDataMove;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatUtils;
import com.github.finley243.adventureengine.stat.StringSetRegistryStat;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.LinkObjectComponent;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;

import java.util.*;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area extends GameInstanced implements Noun, ScriptValueHolder, StatHolder, Effectable {

	public enum RestrictionType {
		PUBLIC, PRIVATE, HOSTILE
	}

	public enum AreaNameType {
		IN, ON, NEAR, FRONT, SIDE, BEHIND
	}

	private final ScriptRuntime scriptRuntime;
	private final UIEventBus eventBus;
	private final MenuManager menuManager;
	private final Pathfinder pathfinder;

	private final WorldObject landmark;
	private final String name;
	private final AreaNameType nameType;
	private final boolean nameIsPlural;
	private boolean isKnown;

	// The room containing this area
	private final Room room;
	
	private final Scene description;

	private final Faction ownerFaction;
	private final RestrictionType restrictionType;
	private final Boolean allowAllies;
	
	// All areas that can be accessed when in this area (key is areaID)
	private final Map<String, AreaLink> linkedAreas;

	private final Map<String, List<Script>> scripts;
	
	// All objects in this area
	private final Set<WorldObject> objects;
	// All actors in this area
	private final Set<Actor> actors;
	// Inventory containing all items in the area (that are not in object or actor inventories)
	private final Inventory itemInventory;

	private final EffectComponent effectComponent;

	private final StringSetRegistryStat<Effect> effects;

	private final Set<ObstructionType> defaultObstructions;
	private final StringSetRegistryStat<ObstructionType> obstructions;
	
	public Area(ScriptRuntime scriptRuntime, UIEventBus eventBus, MenuManager menuManager, Pathfinder pathfinder, Registry<ObstructionType> obstructionTypeRegistry, Registry<Effect> effectRegistry, ItemFactory itemFactory, String ID, WorldObject landmark, String name, AreaNameType nameType, boolean nameIsPlural, Scene description, Room room, Faction ownerFaction, RestrictionType restrictionType, Boolean allowAllies, Map<String, AreaLink> linkedAreas, Set<ObstructionType> defaultObstructions, Map<String, List<Script>> scripts) {
		super(ID);
		this.scriptRuntime = scriptRuntime;
		this.eventBus = eventBus;
		this.menuManager = menuManager;
		this.pathfinder = pathfinder;
		this.landmark = landmark;
		this.name = name;
		this.nameType = nameType;
		this.nameIsPlural = nameIsPlural;
		this.description = description;
		this.room = room;
		this.ownerFaction = ownerFaction;
		this.restrictionType = restrictionType;
		this.allowAllies = allowAllies;
		this.linkedAreas = linkedAreas;
		this.objects = new HashSet<>();
		this.actors = new HashSet<>();
		this.itemInventory = new Inventory(itemFactory, null);
		this.scripts = scripts;
		this.effects = new StringSetRegistryStat<>("effects", this, scriptRuntime, effectRegistry, Effect::getID);
		this.defaultObstructions = defaultObstructions;
		this.obstructions = new StringSetRegistryStat<>("obstructions", this, scriptRuntime, obstructionTypeRegistry, ObstructionType::ID);
		this.effectComponent = new EffectComponent(this, scriptRuntime, Context.builder().parentArea(this).build());
	}

	public void resolveAreaLinks(AreaRegistry areaRegistry) {
		for (Map.Entry<String, AreaLink> entry : linkedAreas.entrySet()) {
			Area area = areaRegistry.getFromID(entry.getKey());
			if (area == null) throw new GameDataException("Area has invalid linked area reference");
			entry.getValue().resolveArea(area);
		}
	}

	private WorldObject getLandmark() {
		return landmark;
	}

	@Override
	public String getName() {
		if (landmark != null) {
			return getLandmark().getName();
		} else if (room != null && name == null) {
			return getRoom().getName();
		} else {
			return name;
		}
	}
	
	public Scene getDescription() {
		return description;
	}

	public Faction getOwnerFaction() {
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
		if (landmark != null) {
			getLandmark().setKnown();
		} else if (room != null && name == null) {
			getRoom().setKnown();
		}
		isKnown = true;
	}

	@Override
	public boolean isKnown() {
		if (landmark != null) {
			return getLandmark().isKnown();
		} else if (room != null && name == null) {
			return getRoom().isKnown();
		} else {
			return isKnown;
		}
	}

	public String getRelativeName() {
		if (landmark == null && name == null && room != null) {
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
		if (landmark == null && name == null && room != null) {
			return getRoom().getMovePhrase(subject);
		}
		return switch (nameType) {
			case IN -> "@moveTo";
			case ON -> "@moveOnto";
			case FRONT -> "@moveFront";
			case BEHIND -> "@moveBehind";
			case SIDE -> "@moveBeside";
			case NEAR -> "@moveToward";
		};
	}

	public void onStartRound() {
		applyEffects();
		effectComponent.onStartRound();
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

	public List<Action> getItemActions(Actor subject, ActionDependencies dependencies) {
		return itemInventory.getAreaActions(subject, dependencies, this);
	}

	public List<Action> getMoveActions(Actor subject, ActionDependencies dependencies, String vehicleType, WorldObject vehicleObject) {
		List<Action> moveActions = new ArrayList<>();
		for (AreaLink link : linkedAreas.values()) {
			if (vehicleType != null && link.isVehicleMovable(vehicleType) || vehicleType == null && link.isMovable()) {
				ActionTemplate actionTemplate = vehicleType == null ? link.getType().getActorMoveAction() : link.getType().getVehicleMoveAction(vehicleType);
				moveActions.add(new ActionCustom(subject, dependencies, null, vehicleObject, null, link.getArea(), actionTemplate, new MapBuilder<String, Script>().put("dir", Script.constant(link.getDirection().toString())).put("dirName", Script.constant(link.getDirection().name)).build(), new MenuDataMove(link.getArea(), link.getDirection()), true));
			}
		}
		return moveActions;
	}

	public Set<Area> getMovableAreas(String vehicleType) {
		Set<Area> movableAreas = new HashSet<>();
		for (AreaLink link : linkedAreas.values()) {
			if (vehicleType != null && link.isVehicleMovable(vehicleType) || vehicleType == null && link.isMovable()) {
				movableAreas.add(link.getArea());
			}
		}
		return movableAreas;
	}

	private Set<String> getMovableAreaIDs(String vehicleType) {
		Set<String> movableAreas = new HashSet<>();
		for (AreaLink link : linkedAreas.values()) {
			if (vehicleType != null && link.isVehicleMovable(vehicleType) || vehicleType == null && link.isMovable()) {
				movableAreas.add(link.getArea().getID());
			}
		}
		return movableAreas;
	}

	public boolean isVisible(Actor subject) {
		return true;
	}

	public Set<AreaLink> getDirectVisibleLinkedAreas() {
		Set<AreaLink> visibleAreas = new HashSet<>();
		for (AreaLink link : linkedAreas.values()) {
			// TODO - Evaluate whether this check is necessary (does not allow non-visible paths to be calculated for reachability)
			if (link.getType().isVisible()) {
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
			LinkObjectComponent linkComponent = object.getComponentOfType(LinkObjectComponent.class);
			if (linkComponent == null) continue;
			visibleAreas.put(object, linkComponent.getLinkedLineOfSightAreas());
		}
		return visibleAreas;
	}

	public boolean hasDirectVisibleLinkTo(Area area) {
		if (linkedAreas.containsKey(area.getID())) {
			AreaLink link = linkedAreas.get(area.getID());
			return link.getType().isVisible();
		}
		for (WorldObject object : getObjects()) {
			LinkObjectComponent linkComponent = object.getComponentOfType(LinkObjectComponent.class);
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
			LinkObjectComponent linkComponent = object.getComponentOfType(LinkObjectComponent.class);
			if (linkComponent == null) continue;
			Map<Area, AreaLink.CompassDirection> visibleAreasWithDirections = linkComponent.getLinkedLineOfSightAreasWithDirections(scriptRuntime);
			if (visibleAreasWithDirections.containsKey(area)) {
				return visibleAreasWithDirections.get(area);
			}
		}
		return null;
	}

	public AreaLink.DistanceCategory getLinearDistanceTo(Area area, Pathfinder pathfinder) {
		if (linkedAreas.containsKey(area.getID())) {
			return linkedAreas.get(area.getID()).getDistance();
		}
		Pathfinder.VisibleAreaData areaData = pathfinder.getLineOfSightAreas(this, Set.of(), true).get(area);
		if (areaData == null) return null;
		return areaData.distance();
	}

	public Set<Area> visibleAreasInRange(Actor subject, Set<AreaLink.DistanceCategory> ranges, Pathfinder pathfinder) {
		Set<Area> areas = new HashSet<>();
		Map<Area, Pathfinder.VisibleAreaData> visibleAreas = pathfinder.getVisibleAreas(this, subject);
		for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : visibleAreas.entrySet()) {
			if (ranges.contains(entry.getValue().distance())) {
				areas.add(entry.getKey());
			}
		}
		return areas;
	}

	public void applyEffects() {
		for (Actor actor : getActors()) {
			for (Effect effect : effects.valueObjects(new HashSet<>(), Context.builder().subject(actor).build())) {
				actor.getEffectComponent().addEffect(effect);
			}
		}
	}

	public Set<ObstructionType> getObstructionTypes() {
		return obstructions.valueObjects(defaultObstructions, Context.builder().parentArea(this).build());
	}

	public boolean hasUnbypassedObstruction(Set<ObstructionType> bypassedObstructionIDs) {
		Set<ObstructionType> activeObstructionIDs = getObstructionTypes();
		return !bypassedObstructionIDs.containsAll(activeObstructionIDs);
	}

	@Override
	public boolean isProperName() {
		if (landmark != null) {
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
        return room;
	}

	public List<Action> getAreaActions(Actor subject, ActionDependencies dependencies) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionInspectArea(subject, dependencies, this, eventBus, menuManager, pathfinder));
		return actions;
	}

	@Override
	public void addEffect(Effect effect) {
		effectComponent.addEffect(effect);
	}

	@Override
	public void removeEffect(Effect effect) {
		effectComponent.removeEffect(effect);
	}

	@Override
	public Expression getScriptValue(String name, Context context) {
		return switch (name) {
			case "inventory" -> Expression.inventory(itemInventory);
			case "noun" -> Expression.noun(this);
			case "id" -> Expression.string(getID());
			case "name" -> Expression.string(getName());
			case "relative_name" -> Expression.string(getRelativeName());
			case "move_phrase" -> Expression.string(getMovePhrase(context.getSubject()));
			case "room" -> Expression.valueHolder(room);
			case "movable_areas" -> Expression.set(getMovableAreas(null), Expression::valueHolder);
			case "obstruction_types" -> Expression.set(StatUtils.objectSetToIDSet(getObstructionTypes(), ObstructionType::ID), Expression::string);
			default -> null;
		};
	}

	@Override
	public boolean setScriptValue(String name, Expression value, Context context) {
		return false;
	}

	@Override
	public Stat getStat(String name) {
		if ("effects".equals(name)) {
			return effects;
		} else if ("obstructions".equals(name)) {
			return obstructions;
		}
		return null;
	}

	@Override
	public void onStatChange(String name) {
		if ("effects".equals(name)) {
			applyEffects();
		}
	}

	public void triggerScript(String entryPoint, Context context) {
		if (scripts.containsKey(entryPoint)) {
			for (Script currentScript : scripts.get(entryPoint)) {
				currentScript.run(scriptRuntime, context);
			}
		}
	}

	public static AreaLink.DistanceCategory pathLengthToDistance(int pathLength) {
		for (AreaLink.DistanceCategory distance : AreaLink.DistanceCategory.values()) {
			if (pathLength >= distance.minPathLength && (distance.maxPathLength == -1 || pathLength <= distance.maxPathLength)) {
				return distance;
			}
		}
		return null;
	}
	
}
