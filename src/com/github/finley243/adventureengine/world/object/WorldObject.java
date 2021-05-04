package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;

public abstract class WorldObject implements Noun, Physical {
	
	private String ID;
	private String name;
	private String areaID;
	
	public WorldObject(String ID, String areaID, String name) {
		this.ID = ID;
		this.name = name;
		this.areaID = areaID;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getFormattedName() {
		return (isProperName() ? "" : "the ") + getName();
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
		return Data.getArea(areaID);
	}

	@Override
	public List<Action> localActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	public String getID() {
		return ID;
	}

}
