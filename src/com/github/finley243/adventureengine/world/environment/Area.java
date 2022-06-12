package com.github.finley243.adventureengine.world.environment;

import java.util.*;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInspectArea;
import com.github.finley243.adventureengine.action.ActionMoveArea;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.WorldObject;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area extends GameInstanced implements Noun {

	public enum AreaNameType {
		IN, ON, NEAR
	}

	private static final boolean FULL_VISIBILITY_IN_ROOM = true;

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
				default:
					return null;
			}
		}
	}

	public String getMovePhrase() {
		if (landmarkID != null) {
			return Phrases.get("moveToward");
		} else {
			switch (nameType) {
				case IN:
					return Phrases.get("moveTo");
				case ON:
					return Phrases.get("moveOnto");
				case NEAR:
				default:
					return Phrases.get("moveToward");
			}
		}
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
					moveActions.add(new ActionMoveArea(game().data().getArea(link.getAreaID()), link));
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
		if (!subject.isUsingObject() || subject.getUsingObject().userCanSeeOtherAreas()) {
			for (AreaLink link : linkedAreas.values()) {
				if (link.getType().isVisible) {
					if (!subject.isInCover()) {
						Area area = game().data().getArea(link.getAreaID());
						visibleAreas.add(area);
					}
				}
			}
		}
		if (FULL_VISIBILITY_IN_ROOM) {
			for (Area area : getRoom().getAreas()) {
				if (!linkedAreas.containsKey(area.getID()) && !subject.isInCover()) {
					visibleAreas.add(area);
				}
			}
		}
		return visibleAreas;
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

	public List<Action> getAreaActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionInspectArea(this));
		return actions;
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
