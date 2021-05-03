package personal.finley.adventure_engine.world.object;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine.Data;
import personal.finley.adventure_engine.action.IAction;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.textgen.Context.Pronoun;
import personal.finley.adventure_engine.world.INoun;
import personal.finley.adventure_engine.world.IPhysical;
import personal.finley.adventure_engine.world.environment.Area;

public abstract class ObjectBase implements INoun, IPhysical {
	
	private String ID;
	private String name;
	private String areaID;
	
	public ObjectBase(String ID, String areaID, String name) {
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
	public List<IAction> localActions(Actor subject) {
		return new ArrayList<IAction>();
	}
	
	@Override
	public List<IAction> remoteActions(Actor subject) {
		return new ArrayList<IAction>();
	}
	
	public String getID() {
		return ID;
	}

}
