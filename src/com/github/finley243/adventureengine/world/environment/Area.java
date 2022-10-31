package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInspectArea;
import com.github.finley243.adventureengine.action.ActionMoveArea;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.AreaEffect;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.*;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area extends GameInstanced implements Noun {

	public enum AreaNameType {
		IN, ON, NEAR, FRONT, BESIDE, BEHIND
	}

	private final String ID;

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

	private final Map<AreaEffect, List<Integer>> areaEffects;
	
	public Area(Game game, String ID, String landmarkID, String name, AreaNameType nameType, Scene description, String roomID, String ownerFaction, boolean isPrivate, Map<String, AreaLink> linkedAreas, Map<String, Script> scripts) {
		super(game);
		if(landmarkID == null && name == null) throw new IllegalArgumentException("Landmark and name cannot both be null: " + ID);
		this.ID = ID;
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
		this.scripts = scripts;
		this.areaEffects = new HashMap<>();
	}
	
	public String getID() {
		return ID;
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
		if(ownerFaction == null) return getRoom().getOwnerFaction();
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

	public String getRelativeName(Area origin) {
		String roomPhrase;
		if (!origin.getRoom().equals(this.getRoom())) {
			roomPhrase = this.getRoom().getRelativeName() + ", ";
		} else {
			roomPhrase = "";
		}
		if (landmarkID != null) {
			return roomPhrase + "near " + getLandmark().getFormattedName();
		} else {
			switch (nameType) {
				case IN:
					return roomPhrase + "in " + getFormattedName();
				case ON:
					return roomPhrase + "on " + getFormattedName();
				case NEAR:
					return roomPhrase + "near " + getFormattedName();
				case FRONT:
					return roomPhrase + "in front of " + getFormattedName();
				case BESIDE:
					return roomPhrase + "beside " + getFormattedName();
				case BEHIND:
					return roomPhrase + "behind " + getFormattedName();
				default:
					return null;
			}
		}
	}

	public AreaLink.CompassDirection getRelativeDirection(Area origin) {
		if (origin.linkedAreas.containsKey(this.getID())) {
			return origin.linkedAreas.get(this.getID()).getDirection();
		}
		return null;
	}

	public String getMovePhrase(Area origin) {
		if (landmarkID != null) {
			return Phrases.get("moveToward");
		} else if (!origin.getRoom().equals(this.getRoom())) {
			return Phrases.get("moveTo");
		} else {
			switch (nameType) {
				case IN:
					return Phrases.get("moveTo");
				case ON:
					return Phrases.get("moveOnto");
				case FRONT:
					return Phrases.get("moveFront");
				case BEHIND:
					return Phrases.get("moveBehind");
				case BESIDE:
					return Phrases.get("moveBeside");
				case NEAR:
				default:
					return Phrases.get("moveToward");
			}
		}
	}

	public void onNewGameInit() {

	}
	
	public Set<WorldObject> getObjects(){
		return objects;
	}

	public Set<WorldObject> getObjectsExcludeLandmark() {
		Set<WorldObject> nonLandmarkObjects = new HashSet<>(objects);
		nonLandmarkObjects.remove(getLandmark());
		return nonLandmarkObjects;
	}
	
	public void addObject(WorldObject object) {
		boolean didAdd = objects.add(object);
		if(!didAdd) {
			System.out.println("Area " + ID + " already contains object " + object + ".");
		}
	}
	
	public void removeObject(WorldObject object) {
		boolean didRemove = objects.remove(object);
		if(!didRemove) {
			System.out.println("Area " + ID + " does not contain object " + object + ".");
		}
	}
	
	public Set<Actor> getActors(){
		return actors;
	}

	public Set<Actor> getActors(Actor exclude) {
		Set<Actor> actorsExclude = new HashSet<>(actors);
		if (exclude != null) {
			actorsExclude.remove(exclude);
		}
		return actorsExclude;
	}

	public String getActorList(Actor exclude) {
		StringBuilder actorList = new StringBuilder();
		boolean firstActor = true;
		for(Actor actor : getActors(exclude)) {
			if(!firstActor) {
				actorList.append(", ");
			} else {
				firstActor = false;
			}
			actorList.append(actor.getName());
		}
		return actorList.toString();
	}
	
	public void addActor(Actor actor) {
		boolean didAdd = actors.add(actor);
		if(!didAdd) {
			System.out.println("Area " + ID + " already contains actor " + actor + ".");
		}
	}
	
	public void removeActor(Actor actor) {
		boolean didRemove = actors.remove(actor);
		if(!didRemove) {
			System.out.println("Area " + ID + " does not contain actor " + actor + ".");
		}
	}

	public Set<AttackTarget> getAttackTargets() {
		Set<AttackTarget> targets = new HashSet<>(getActors());
		for (WorldObject object : getObjects()) {
			if (object instanceof AttackTarget) {
				targets.add((AttackTarget) object);
			}
		}
		return targets;
	}

	public List<Action> getMoveActions() {
		List<Action> moveActions = new ArrayList<>();
		for(AreaLink link : linkedAreas.values()) {
			if(link.getDistance().isMovable) {
				moveActions.add(new ActionMoveArea(game().data().getArea(link.getAreaID()), link));
			}
		}
		return moveActions;
	}

	public Set<Area> getMovableAreas() {
		Set<Area> movableAreas = new HashSet<>();
		for(AreaLink link : linkedAreas.values()) {
			if((link.getDistance().isMovable)) {
				movableAreas.add(game().data().getArea(link.getAreaID()));
			}
		}
		return movableAreas;
	}

	public Set<Area> getVisibleAreas(Actor subject) {
		Set<Area> visibleAreas = new HashSet<>();
		visibleAreas.add(this);
		if (!subject.isUsingObject() || subject.getUsingObject().userCanSeeOtherAreas()) {
			for (AreaLink link : linkedAreas.values()) {
				if (link.isVisible()) {
					if (!subject.isInCover()) {
						Area area = game().data().getArea(link.getAreaID());
						visibleAreas.add(area);
					}
				}
			}
			for (RoomLink roomLink : getRoom().getLinkedRooms().values()) {
				visibleAreas.addAll(game().data().getRoom(roomLink.getRoomID()).getAreas());
			}
		}
		return visibleAreas;
	}

	public boolean isVisible(Actor subject, String areaID) {
		if (linkedAreas.containsKey(areaID)) {
			AreaLink link = linkedAreas.get(areaID);
			return link.isVisible();
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

	public void addAreaEffect(AreaEffect areaEffect) {
		if (!areaEffects.containsKey(areaEffect)) {
			areaEffects.put(areaEffect, new ArrayList<>());
		}
		areaEffects.get(areaEffect).add(0);
		// TODO - Start non-actor effects
		for (Actor actor : getActors()) {
			if (actor.effectComponent() != null) {
				for (Effect effect : areaEffect.getEffects()) {
					actor.effectComponent().addEffect(effect);
				}
			}
		}
	}

	public void onStartRound() {
		Iterator<AreaEffect> itr = areaEffects.keySet().iterator();
		while (itr.hasNext()) {
			AreaEffect areaEffect = itr.next();
			for (Actor actor : getActors()) {
				if (actor.effectComponent() != null) {
					for (Effect effect : areaEffect.getEffects()) {
						actor.effectComponent().addEffect(effect);
					}
				}
			}
			List<Integer> counters = areaEffects.get(areaEffect);
			for (int i = 0; i < counters.size(); i++) {
				int counterValue = counters.get(i) + 1;
				counters.set(i, counterValue);
				if (counterValue == areaEffect.getDuration()) {
					// TODO - End non-actor effects
					counters.remove(0);
					if (counters.isEmpty()) {
						itr.remove();
					}
				}
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

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if(scripts.containsKey(entryPoint)) {
			scripts.get(entryPoint).execute(subject, target);
		}
	}

	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "isKnown":
				if(saveData.getValueBoolean()) {
					setKnown();
				}
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(isKnown()) {
			state.add(new SaveData(SaveData.DataType.AREA, this.getID(), "isKnown", isKnown()));
		}
		return state;
	}
	
	@Override
	public String toString() {
		return getID();
	}
	
	@Override
	public int hashCode() {
		return ID.hashCode();
	}
	
}
