package personal.finley.adventure_engine_2.world.object;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.EnumTypes.Pronoun;
import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.world.IActionable;
import personal.finley.adventure_engine_2.world.INoun;
import personal.finley.adventure_engine_2.world.environment.Area;

public class ObjectBase implements INoun, IActionable {

	private String ID;
	private String name;
	private String currentAreaID;
	
	public ObjectBase(String ID, String name) {
		this.ID = ID;
		this.name = name;
		Data.addObject(ID, this);
	}
	
	@Override
	public String getName() {
		return name;
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
		return Data.getArea(currentAreaID);
	}
	
	public void setAreaID(String areaID) {
		this.currentAreaID = areaID;
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
