package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInspect;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;

/**
 * An object that can exist in the game world
 */
public abstract class WorldObject extends GameInstanced implements Noun, Physical {

	private final String ID;
	private final String name;
	private boolean isKnown;
	private boolean isEnabled;
	private final Area defaultArea;
	private Area area;
	private final String description;
	private final Map<String, Script> scripts;
	
	public WorldObject(Game gameInstance, String ID, Area area, String name, String description, Map<String, Script> scripts) {
		super(gameInstance);
		this.ID = ID;
		this.defaultArea = area;
		this.area = area;
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		setEnabled(true);
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
	public String getFormattedName() {
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), !isKnown);
		} else {
			return getName();
		}
	}

	@Override
	public void setKnown() {
		isKnown = true;
	}

	@Override
	public boolean isKnown() {
		return isKnown;
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
	public boolean forcePronoun() {
		return false;
	}
	
	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		this.area = area;
	}

	public void setEnabled(boolean enable) {
		if(area != null && isEnabled != enable) {
			isEnabled = enable;
			if(enable) {
				area.addObject(this);
			} else {
				area.removeObject(this);
			}
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
	public List<Action> adjacentActions(Actor subject) {
		return new ArrayList<>();
	}

	public void triggerScript(String entryPoint, Actor subject) {
		if(scripts.containsKey(entryPoint)) {
			scripts.get(entryPoint).execute(subject);
		}
	}

	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "isKnown":
				this.isKnown = saveData.getValueBoolean();
				break;
			case "isEnabled":
				setEnabled(saveData.getValueBoolean());
				break;
			case "area":
				this.area = game().data().getArea(saveData.getValueString());
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(isKnown) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "isKnown", isKnown));
		}
		if(!isEnabled) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "isEnabled", isEnabled));
		}
		if(area != defaultArea) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "area", area.getID()));
		}
		return state;
	}

	@Override
	public int hashCode() {
		return ID.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WorldObject && ((WorldObject) o).getID().equals(this.getID());
	}

	@Override
	public String toString() {
		return this.getID();
	}

}
