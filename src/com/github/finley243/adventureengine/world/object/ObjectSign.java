package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionReadSign;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.Readable;

public class ObjectSign extends WorldObject implements Readable {

	private List<String> text;
	
	public ObjectSign(String name, List<String> text) {
		super(name);
		this.text = text;
	}
	
	@Override
	public List<String> getText() {
		return text;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionReadSign(this));
		return actions;
	}

}
