package com.github.finley243.adventureengine.world.object;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionReadSign;
import com.github.finley243.adventureengine.actor.Actor;

public class ObjectSign extends WorldObject {

	private String signText;
	
	public ObjectSign(String name, String signText) {
		super(name);
		this.signText = signText;
	}
	
	public String getSignText() {
		return signText;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionReadSign(this));
		return actions;
	}

}
