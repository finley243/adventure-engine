package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInspect;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;

/**
 * A static object that can exist in the game world
 */
public abstract class WorldObject implements Noun, Physical {

	private final String ID;
	private final String name;
	private Area area;
	private final String description;
	
	public WorldObject(String ID, String name, String description) {
		this.ID = ID;
		this.name = name;
		this.description = description;
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
	
	@Override
	public boolean isProperName() {
		return false;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}
	
	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		this.area = area;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		if(description != null) {
			actions.add(new ActionInspect(this, InspectType.WORLD));
		}
		return actions;
	}

	@Override
	public List<Action> adjacentActions(Actor subject) {
		return new ArrayList<>();
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<>();
	}

	@Override
	public int hashCode() {
		return ID.hashCode();
	}

	private void actionInspect() {

	}

}
