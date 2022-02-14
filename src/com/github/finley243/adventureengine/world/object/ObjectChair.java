package com.github.finley243.adventureengine.world.object;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.action.ActionUseStop;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;

public class ObjectChair extends UsableObject {

    public ObjectChair(String ID, String name, String description, Map<String, Script> scripts) {
        super(ID, name, description, scripts);
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
	public String getStartPromptFull() {
		return "Sit in " + getFormattedName(false);
	}

	@Override
	public String getStopPromptFull() {
		return "Stand up";
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
