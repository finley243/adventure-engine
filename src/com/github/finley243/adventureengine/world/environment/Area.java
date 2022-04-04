package com.github.finley243.adventureengine.world.environment;

import java.util.*;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveArea;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.WorldObject;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area extends GameInstanced implements Noun {

	private final String ID;

	private final String landmarkID;
	// The room containing this area
	private final String roomID;
	
	private final String description;

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
	
	public Area(Game game, String ID, String landmarkID, String description, String roomID, String ownerFaction, boolean isPrivate, Map<String, AreaLink> linkedAreas, Map<String, Script> scripts) {
		super(game);
		this.ID = ID;
		this.landmarkID = landmarkID;
		this.description = description;
		this.roomID = roomID;
		this.ownerFaction = ownerFaction;
		this.isPrivate = isPrivate;
		this.linkedAreas = linkedAreas;
		this.objects = new HashSet<>();
		this.actors = new HashSet<>();
		this.scripts = scripts;
	}
	
	public String getID() {
		return ID;
	}

	public WorldObject getLandmark() {
		return game().data().getObject(landmarkID);
	}

	@Override
	public String getName() {
		return getLandmark().getName();
		//return getFormattedName();
	}
	
	public String getDescription() {
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
		return getLandmark().getFormattedName();
	}

	public void setKnown() {
		getLandmark().setKnown();
	}

	@Override
	public boolean isKnown() {
		return getLandmark().isKnown();
	}

	public String getRelativeName() {
		return "near " + getLandmark().getFormattedName();
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
		actorsExclude.remove(exclude);
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

	public Set<Area> getNearAreas() {
		Set<Area> nearAreas = new HashSet<>();
		for(AreaLink link : linkedAreas.values()) {
			if(link.getDistance() == 0) {
				nearAreas.add(game().data().getArea(link.getAreaID()));
			}
		}
		return nearAreas;
	}

	public List<Action> getMoveActions() {
		List<Action> moveActions = new ArrayList<>();
		for(AreaLink link : linkedAreas.values()) {
			if(link.getType().isMovable) {
				if(link.heightChange() == 0) {
					moveActions.add(new ActionMoveArea(game().data().getArea(link.getAreaID()), link.getDirection()));
				}
			}
		}
		return moveActions;
	}

	public Set<Area> getMovableAreas() {
		Set<Area> movableAreas = new HashSet<>();
		for(AreaLink link : linkedAreas.values()) {
			if((link.getType().isMovable) && link.heightChange() == 0) {
				movableAreas.add(game().data().getArea(link.getAreaID()));
			}
		}
		return movableAreas;
	}

	public Set<Area> getVisibleAreas(Actor subject) {
		Set<Area> visibleAreas = new HashSet<>();
		visibleAreas.add(this);
		Set<AreaLink.RelativeDirection> obstructedDirections = EnumSet.noneOf(AreaLink.RelativeDirection.class);
		for(WorldObject object : getObjects()) {
			if(object instanceof ObjectCover) {
				obstructedDirections.addAll(Arrays.asList(((ObjectCover) object).getDirection().obstructsTo));
			}
		}
		for(AreaLink link : linkedAreas.values()) {
			if(link.getType().isVisible) {
				if (!(subject.isCrouching() && obstructedDirections.contains(link.getDirection()))) {
					Area area = game().data().getArea(link.getAreaID());
					visibleAreas.add(area);
				}
			}
		}
		return visibleAreas;
	}

	public boolean isBehindCover(Area target) {
		if (linkedAreas.containsKey(target.getID())) {
			for (WorldObject object : target.getObjects()) {
				if (object instanceof ObjectCover && ((ObjectCover) object).obstructsFrom(linkedAreas.get(target.getID()).getDirection())) {
					return true;
				}
			}
		}
		return false;
	}

	public AreaLink.RelativeDirection getRelativeDirectionOf(Area other) {
		if(!linkedAreas.containsKey(other.getID())) return null;
		return linkedAreas.get(other.getID()).getDirection();
	}

	public boolean isVisible(String areaID) {
		if(!linkedAreas.containsKey(areaID)) {
			return false;
		}
		AreaLink link = linkedAreas.get(areaID);
		return link.getType().isVisible;
	}

	public int getDistanceTo(String areaID) {
		if(!linkedAreas.containsKey(areaID)) return -1;
		return linkedAreas.get(areaID).getDistance();
	}

	public Set<Area> visibleAreasInRange(int rangeMin, int rangeMax) {
		Set<Area> areas = new HashSet<>();
		for(AreaLink link : linkedAreas.values()) {
			if(isVisible(link.getAreaID()) && getDistanceTo(link.getAreaID()) >= rangeMin && getDistanceTo(link.getAreaID()) <= rangeMax) {
				areas.add(game().data().getArea(link.getAreaID()));
			}
		}
		return areas;
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

	public void triggerScript(String entryPoint, Actor subject) {
		if(scripts.containsKey(entryPoint)) {
			scripts.get(entryPoint).execute(subject);
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
