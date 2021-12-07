package com.github.finley243.adventureengine.world.environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.object.WorldObject;

/**
 * Represents a section of a room that can contain objects and actors
 */
public class Area implements Noun {

	public enum AreaNameType {
		ABS, NEAR, LEFT, RIGHT, FRONT, BEHIND, BETWEEN
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
	// Coordinates in room
	private final int x;
	private final int y;
	
	private final String description;
	
	// All objects in this area
	private final Set<WorldObject> objects;
	// All actors in this area
	private final Set<Actor> actors;
	
	public Area(String ID, String name, String description, boolean isProperName, AreaNameType nameType, String roomID, int x, int y, Set<WorldObject> objects) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.isProperName = isProperName;
		this.nameType = nameType;
		this.roomID = roomID;
		this.x = x;
		this.y = y;
		this.objects = objects;
		this.actors = new HashSet<>();
	}

	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return name + " (" + x + ", " + y + ")";
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
			case ABS:
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
			case BETWEEN:
				return "between " + getFormattedName(false);
		}
	}

	public String getMoveDescription() {
		if(nameType == AreaNameType.ABS) {
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
	
	public Set<Area> getLinkedAreas() {
		Set<Area> output = new HashSet<>();
		Room room = Data.getRoom(roomID);
		Area left = room.getArea(x - 1, y);
		Area right = room.getArea(x + 1, y);
		Area up = room.getArea(x, y + 1);
		Area down = room.getArea(x, y - 1);
		if(left != null) {
			output.add(left);
		}
		if(right != null) {
			output.add(right);
		}
		if(up != null) {
			output.add(up);
		}
		if(down != null) {
			output.add(down);
		}
		return output;
	}
	
	public Set<Area> getVisibleAreas() {
		// TODO - Implement visibility system using obstructions
		Area[][] roomAreas = Data.getRoom(roomID).getAreas();
		Set<Area> visibleAreas = new HashSet<>();
		for(Area[] current : roomAreas) {
			visibleAreas.addAll(Arrays.asList(current));
		}
		visibleAreas.remove(null);
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

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int hashCode() {
		return ID.hashCode();
	}
	
}
