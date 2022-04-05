package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.action.ActionUseStop;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;

/**
 * An object that can have a single "user" (e.g. a chair)
 */
public abstract class UsableObject extends WorldObject {

	private Actor user;

	public UsableObject(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts) {
		super(game, ID, area, name, description, scripts);
	}

	public abstract String getStartPhrase();

	public abstract String getStopPhrase();

	public abstract String getStartPrompt();

	public abstract String getStopPrompt();

	public boolean isAvailableToUse() {
		return user == null;
	}

	public Actor getUser() {
		return user;
	}

	public void setUser(Actor user) {
		this.user = user;
	}
	
	public void removeUser() {
		this.user = null;
	}

	public boolean userInCover() {
		return false;
	}

	public boolean userHidden() {
		return false;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if(isAvailableToUse() && !subject.isUsingObject()) {
			actions.add(new ActionUseStart(this));
		}
		return actions;
	}

	public List<Action> usingActions() {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionUseStop(this));
		return actions;
	}

	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "user":
				user = game().data().getActor(saveData.getValueString());
				break;
			default:
				super.loadState(saveData);
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if(user != null) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "user", user.getID()));
		}
		return state;
	}
	
}
