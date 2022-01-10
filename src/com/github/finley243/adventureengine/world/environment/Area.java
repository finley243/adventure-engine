package com.github.finley243.adventureengine.world.environment;

import java.util.*;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveArea;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.WorldObject;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area implements Noun {

	public enum AreaNameType {
		IN, NEAR, LEFT, RIGHT, FRONT, BEHIND, ON, AGAINST
	}

	private final String ID;
	
	// The name of the area
	private final String name;
	// Whether the name is a proper name (if false, should be preceded with "the" or "a")
	private final boolean isProperName;
	// Format used to describe the area (abs: "move to [name]", near: "move near [name]", behind:"move behind [name]", etc.)
	private final AreaNameType nameType;
	// The room containing this area
	private final String roomID;
	
	private final String description;

	private final String ownerFaction;
	// Whether members and allies of ownerFaction should react negatively to non-allies in area
	private final boolean isPrivate;
	
	// All areas that can be accessed when in this area (key is areaID)
	private final Map<String, AreaLink> linkedAreas;
	
	// All objects in this area
	private final Set<WorldObject> objects;
	// All actors in this area
	private final Set<Actor> actors;
	
	public Area(String ID, String name, String description, boolean isProperName, AreaNameType nameType, String roomID, String ownerFaction, boolean isPrivate, Map<String, AreaLink> linkedAreas, Set<WorldObject> objects) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.isProperName = isProperName;
		this.nameType = nameType;
		this.roomID = roomID;
		this.ownerFaction = ownerFaction;
		this.isPrivate = isPrivate;
		this.linkedAreas = linkedAreas;
		this.objects = objects;
		this.actors = new HashSet<>();
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return getFormattedName(false);
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
	public String getFormattedName(boolean indefinite) {
		String formattedName;
		if(!isProperName()) {
			formattedName = LangUtils.addArticle(name, indefinite);
		} else {
			formattedName = name;
		}
		switch(nameType) {
			case IN:
			case ON:
			default:
				return formattedName;
			case NEAR:
				return "near " + formattedName;
			case LEFT:
				return "to the left of " + formattedName;
			case RIGHT:
				return "to the right of " + formattedName;
			case FRONT:
				return "in front of " + formattedName;
			case BEHIND:
				return "behind " + formattedName;
			case AGAINST:
				return "against " + formattedName;
		}
	}

	public String getRelativeName() {
		if(nameType == AreaNameType.IN) {
			return "in " + getFormattedName(false);
		} else if(nameType == AreaNameType.ON) {
			return "on " + getFormattedName(false);
		} else {
			return getFormattedName(false);
		}
	}

	public String getMoveDescription() {
		if(nameType == AreaNameType.IN || nameType == AreaNameType.ON) {
			return "to " + getFormattedName(false);
		} else {
			return getFormattedName(false);
		}
	}

	public String getMovePhrase() {
		if(nameType == AreaNameType.IN || nameType == AreaNameType.ON) {
			return "moveTo";
		} else {
			return "move";
		}
	}
	
	public Set<WorldObject> getObjects(){
		return objects;
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
				nearAreas.add(Data.getArea(link.getAreaID()));
			}
		}
		return nearAreas;
	}

	public List<Action> getMoveActions() {
		List<Action> moveActions = new ArrayList<>();
		for(AreaLink link : linkedAreas.values()) {
			if(link.getType() == AreaLink.AreaLinkType.DEFAULT || link.getType() == AreaLink.AreaLinkType.MOVE) {
				if(link.heightChange() == 0) {
					moveActions.add(new ActionMoveArea(Data.getArea(link.getAreaID()), link.getDirection()));
				}
			}
		}
		return moveActions;
	}

	public Set<Area> getMovableAreas() {
		Set<Area> movableAreas = new HashSet<>();
		for(AreaLink link : linkedAreas.values()) {
			if((link.getType() == AreaLink.AreaLinkType.DEFAULT || link.getType() == AreaLink.AreaLinkType.MOVE) && link.heightChange() == 0) {
				movableAreas.add(Data.getArea(link.getAreaID()));
			}
		}
		return movableAreas;
	}

	public Set<Area> getVisibleAreas(Actor subject) {
		Set<Area> visibleAreas = new HashSet<>();
		visibleAreas.add(this);
		for(AreaLink link : linkedAreas.values()) {
			if(link.getType() != AreaLink.AreaLinkType.MOVE) {
				// TODO - Redesign inefficient check
				boolean obstructed = false;
				if (subject.isCrouching()) {
					for (WorldObject object : getObjects()) {
						if (object instanceof ObjectCover && ((ObjectCover) object).obstructsTo(link.getDirection())) {
							obstructed = true;
							break;
						}
					}
				}
				if (!obstructed) {
					Area area = Data.getArea(link.getAreaID());
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
				areas.add(Data.getArea(link.getAreaID()));
			}
		}
		return areas;
	}

	@Override
	public boolean isProperName() {
		return isProperName;
	}

	public AreaNameType getNameType() {
		return nameType;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}
	
	public Room getRoom() {
		return Data.getRoom(roomID);
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
