package com.github.finley243.adventureengine.world.environment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.object.WorldObject;

/**
 * Represents a self-contained space (e.g. an actual room) that contains smaller areas
 */
public class Room implements Noun {

	private final String ID;
	private final String name;
	private final boolean isProperName;
	private final String description;
	private final List<String> scenes;
	private final String ownerFaction;
	// null = not part of room
	private final Area[][] roomGrid;

	private boolean hasVisited;

	public Room(String ID, String name, boolean isProperName, String description, List<String> scenes, String ownerFaction, int xDim, int yDim, Set<WorldObject> objects) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.description = description;
		this.scenes = scenes;
		this.ownerFaction = ownerFaction;
		this.roomGrid = new Area[xDim][yDim];
		for(int x = 0; x < xDim; x++) {
			for(int y = 0; y < yDim; y++) {
				Area area = new Area(name + "_" + x + "_" + y, name, null, false, Area.AreaNameType.ABS, ID, x, y, new HashSet<>());
				roomGrid[x][y] = area;
				Data.addArea(area.getID(), area);
			}
		}
		this.hasVisited = false;
	}
	
	public String getID() {
		return ID;
	}
	
	public Area[][] getAreas(){
		return roomGrid;
	}

	public Area getArea(int x, int y) {
		if(x < 0 || x > roomGrid.length - 1 || y < 0 || y > roomGrid[0].length - 1) return null;
		return roomGrid[x][y];
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean hasVisited() {
		return hasVisited;
	}
	
	public void setVisited() {
		hasVisited = true;
	}

	public List<String> getScenes() {
		return scenes;
	}

	public String getOwnerFaction() {
		return ownerFaction;
	}
	
	public Set<WorldObject> getObjects() {
		Set<WorldObject> objects = new HashSet<>();
		for(Area[] current : roomGrid) {
			for(Area area : current) {
				objects.addAll(area.getObjects());
			}
		}
		return objects;
	}
	
	public Set<Actor> getActors() {
		Set<Actor> actors = new HashSet<>();
		for(Area[] current : roomGrid) {
			for(Area area : current) {
				actors.addAll(area.getActors());
			}
		}
		return actors;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getFormattedName(boolean indefinite) {
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), indefinite);
		} else {
			return getName();
		}
	}

	@Override
	public boolean isProperName() {
		return isProperName;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}
	
}
