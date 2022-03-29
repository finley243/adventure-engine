package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.item.Item;

/**
 * An object that can have a single "user" (e.g. a chair)
 */
public abstract class UsableObject extends WorldObject {

	private Actor user;

	public UsableObject(Game game, String ID, String name, String description, Map<String, Script> scripts) {
		super(game, ID, name, description, scripts);
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
	
	public List<Action> usingActions() {
		return new ArrayList<>();
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
