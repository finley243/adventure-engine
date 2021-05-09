package com.github.finley243.adventureengine.world.environment;

import java.util.HashSet;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class Room implements Noun {

	private String ID;
	
	private String name;
	private boolean isProperName;
	private String description;
	
	private boolean hasVisited;
	
	private Set<Area> areas;
	
	public Room(String ID, String name, boolean isProperName, String description, Set<Area> areas) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.description = description;
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
	
	public Set<WorldObject> getObjects() {
		Set<WorldObject> objects = new HashSet<WorldObject>();
		for(Area area : areas) {
			objects.addAll(area.getObjects());
		}
		return objects;
	}
	
	public Set<Actor> getActors() {
		Set<Actor> actors = new HashSet<Actor>();
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
		if(isProperName()) {
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
