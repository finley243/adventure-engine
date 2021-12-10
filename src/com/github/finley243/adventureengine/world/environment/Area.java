package com.github.finley243.adventureengine.world.environment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
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
	
	// All areas that can be accessed when in this area (key is areaID)
	private final Map<String, AreaLink> linkedAreas;
	
	// All objects in this area
	private final Set<WorldObject> objects;
	// All actors in this area
	private final Set<Actor> actors;
	
	public Area(String ID, String name, String description, boolean isProperName, AreaNameType nameType, String roomID, Map<String, AreaLink> linkedAreas, Set<WorldObject> objects) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.isProperName = isProperName;
		this.nameType = nameType;
		this.roomID = roomID;
		this.linkedAreas = linkedAreas;
		this.objects = objects;
		this.actors = new HashSet<>();
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return getRelativeName();
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getFormattedName(boolean indefinite) {
		/*if(!isProperName()) {
			return LangUtils.addArticle(name, indefinite);
		} else {
			return name;
		}*/
		return getRelativeName();
	}

	public String getRelativeName() {
		String formattedName;
		if(!isProperName()) {
			formattedName = LangUtils.addArticle(name, false);
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

	public String getMoveDescription() {
		if(nameType == AreaNameType.IN || nameType == AreaNameType.ON) {
			return "to " + getRelativeName();
		} else {
			return getRelativeName();
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
	
	public Set<Area> getMovableAreas() {
		Set<Area> output = new HashSet<>();
		for(AreaLink link : linkedAreas.values()) {
			if(link.getType() == AreaLink.AreaLinkType.MOVE && link.heightChange() == 0) {
				output.add(Data.getArea(link.getAreaID()));
			}
		}
		return output;
	}

	public Set<Area> getVisibleAreas(Actor subject) {
		Set<Area> visibleAreas = new HashSet<>();
		visibleAreas.add(this);
		for(AreaLink link : linkedAreas.values()) {
			boolean obstructed = false;
			if(subject.isCrouching()) {
				for (WorldObject object : getObjects()) {
					if (object instanceof ObjectCover && ((ObjectCover) object).obstructsTo(link.getDirection())) {
						obstructed = true;
						break;
					}
				}
			}
			if(!obstructed) {
				Area area = Data.getArea(link.getAreaID());
				visibleAreas.add(area);
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
