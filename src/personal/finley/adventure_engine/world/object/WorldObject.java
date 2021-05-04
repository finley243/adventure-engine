package personal.finley.adventure_engine.world.object;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine.Data;
import personal.finley.adventure_engine.action.Action;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.textgen.Context.Pronoun;
import personal.finley.adventure_engine.world.Noun;
import personal.finley.adventure_engine.world.Physical;
import personal.finley.adventure_engine.world.environment.Area;

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
