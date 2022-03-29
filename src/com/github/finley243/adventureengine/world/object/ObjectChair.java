package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.action.ActionUseStop;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public class ObjectChair extends UsableObject {

    public ObjectChair(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts) {
        super(game, ID, area, name, description, scripts);
    }

	@Override
	public String getStartPhrase() {
		return "sit";
	}

	@Override
	public String getStopPhrase() {
		return "stand";
	}

	@Override
	public String getStartPrompt() {
		return "Sit";
	}

	@Override
	public String getStopPrompt() {
		return "Stand";
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = super.localActions(subject);
		if(isAvailableToUse() && !subject.isUsingObject()) {
			actions.add(new ActionUseStart(this));
		}
		return actions;
	}

	@Override
	public List<Action> usingActions() {
		List<Action> actions = super.usingActions();
		actions.add(new ActionUseStop(this));
		return actions;
	}
    
}
