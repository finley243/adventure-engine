package com.github.finley243.adventureengine.world.environment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	private boolean hasVisited;
	
	private final Set<Area> areas;
	
	public Room(String ID, String name, boolean isProperName, String description, List<String> scenes, Set<Area> areas) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.description = description;
		this.scenes = scenes;
		this.areas = areas;
		this.hasVisited = false;
	}
	
	public String getID() {
		return ID;
	}
	
	public Set<Area> getAreas(){
		return areas;
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
	
	public Set<WorldObject> getObjects() {
		Set<WorldObject> objects = new HashSet<>();
		for(Area area : areas) {
			objects.addAll(area.getObjects());
		}
		return objects;
	}
	
	public Set<Actor> getActors() {
		Set<Actor> actors = new HashSet<>();
		for(Area area : areas) {
			actors.addAll(area.getActors());
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
