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
import com.github.finley243.adventureengine.world.environment.Room;

/**
 * A static object that can exist in the game world
 */
public abstract class WorldObject extends Physical implements Noun {
	
	private final String name;
	private Room room;
	private final String description;
	
	public WorldObject(String name, String description) {
		super(1, 1);
		this.name = name;
		this.description = description;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	// Partial obstruction = no movement, allows visibility
	public boolean isPartialObstruction() {
		return false;
	}

	// Full obstruction = no movement, no visibility
	public boolean isFullObstruction() {
		return false;
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
	public void setPosition(Room room, int x, int y) {
		for(Area area : getAreas()) {
			area.removeObject(this);
		}
		super.setPosition(room, x, y);
		for(Area area : getAreas()) {
			area.addObject(this);
		}
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
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<>();
	}

	@Override
	public void executeAction(String action, Actor subject) {
		switch(action.toUpperCase()) {
			case "INSPECT":
				actionInspect();
				break;
			default:
				throw new IllegalArgumentException("Action " + action + " does not exist for object " + this.getClass().getSimpleName());
		}
	}

	private void actionInspect() {

	}

}
