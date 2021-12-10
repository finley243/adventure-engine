package com.github.finley243.adventureengine.world.environment;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.object.ObjectObstruction;
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
	
	// All areas that can be accessed when in this area
	private final Set<AreaLink> linkedAreas;
	
	// All objects in this area
	private final Set<WorldObject> objects;
	// All actors in this area
	private final Set<Actor> actors;
	
	public Area(String ID, String name, String description, boolean isProperName, AreaNameType nameType, String roomID, Set<AreaLink> linkedAreas, Set<WorldObject> objects) {
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
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getFormattedName(boolean indefinite) {
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), indefinite);
		} else {
			return getName();
		}
	}

	public String getRelativeName() {
		switch(nameType) {
			case IN:
			case ON:
			default:
				return getFormattedName(false);
			case NEAR:
				return "near " + getFormattedName(false);
			case LEFT:
				return "to the left of " + getFormattedName(false);
			case RIGHT:
				return "to the right of " + getFormattedName(false);
			case FRONT:
				return "in front of " + getFormattedName(false);
			case BEHIND:
				return "behind " + getFormattedName(false);
			case AGAINST:
				return "against " + getFormattedName(false);
		}
	}

	public String getMoveDescription() {
		if(nameType == AreaNameType.IN || nameType == AreaNameType.ON) {
			return "to " + getFormattedName(false);
		} else {
			return getRelativeName();
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
		for(AreaLink link : linkedAreas) {
			// Only set as a "moveable" area if the height is equal to the current area
			if(link.heightChange() == 0) {
				output.add(Data.getArea(link.getAreaID()));
			}
		}
		return output;
	}
	
	public Set<Area> getVisibleAreas() {
		Set<Area> visibleAreas = new HashSet<>();
		visibleAreas.add(this);
		Set<String> visited = new HashSet<>();
		for(AreaLink link : linkedAreas) {
			Area linkedArea = Data.getArea(link.getAreaID());
			visibleAreas.addAll(linkedArea.getVisibleAreas(link.getRelativeDirection(), visited));
		}
		System.out.println("Visible Areas: " + visibleAreas);
		return visibleAreas;
	}

	private Set<Area> getVisibleAreas(AreaLink.RelativeDirection direction, Set<String> visited) {
		//System.out.println("getVisibleAreas - area: " + this.getID() + ", direction: " + direction);
		visited.add(this.getID());
		//System.out.println("visited: " + visited);
		Set<Area> visibleAreas = new HashSet<>();
		for(WorldObject object : this.getObjects()) {
			if(object instanceof ObjectObstruction && ((ObjectObstruction) object).obstructsFrom(direction)) {
				//System.out.println("Blocked by obstruction!");
				return visibleAreas;
			}
		}
		visibleAreas.add(this);
		for(AreaLink link : linkedAreas) {
			AreaLink.RelativeDirection combinedDirection = AreaLink.combinedDirection(direction, link.getRelativeDirection());
			if(combinedDirection != null) {
				Area area = Data.getArea(link.getAreaID());
				if(!visited.contains(area.getID())) {
					//System.out.println("Recursive call opened - " + area.getID());
					visibleAreas.addAll(area.getVisibleAreas(combinedDirection, visited));
					//System.out.println("Recursive call closed - " + area.getID());
				}
			}
		}
		//System.out.println("Partial visibleAreas (from " + this.getID() + "): " + visibleAreas);
		return visibleAreas;
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
