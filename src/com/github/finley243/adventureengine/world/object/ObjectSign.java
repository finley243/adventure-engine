package com.github.finley243.adventureengine.world.object;

import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionReadSign;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectSign extends WorldObject {

	private final List<String> text;
	
	public ObjectSign(String ID, String name, String description, List<String> text) {
		super(ID, name, description);
		this.text = text;
	}

	public List<String> getText() {
		return text;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.add(new ActionReadSign(this, false));
		return actions;
	}

}
