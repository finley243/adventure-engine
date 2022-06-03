package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionReadSign;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectSign extends WorldObject {

	private final List<String> text;
	
	public ObjectSign(Game game, String ID, Area area, String name, Scene description, Map<String, Script> scripts, List<ActionCustom> customActions, List<String> text) {
		super(game, ID, area, name, description, scripts, customActions);
		this.text = text;
	}

	public List<String> getText() {
		return text;
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		actions.add(new ActionReadSign(this));
		return actions;
	}

}
