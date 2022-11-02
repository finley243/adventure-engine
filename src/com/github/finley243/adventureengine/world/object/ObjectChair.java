package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.ComponentLink;

import java.util.List;
import java.util.Map;

public class ObjectChair extends UsableObject {

    public ObjectChair(Game game, String ID, String templateID, Area area, boolean startDisabled, boolean startHidden, Map<String, ComponentLink> linkedObjects, List<ActionCustom> customUsingActions) {
        super(game, ID, templateID, area, startDisabled, startHidden, linkedObjects, customUsingActions);
    }

	@Override
	public String getStartPhrase() {
		return "sit";
	}

	@Override
	public String getStopPhrase() {
		return "getUp";
	}

	@Override
	public String getStartPrompt() {
		return "Sit";
	}

	@Override
	public String getStopPrompt() {
		return "Stand";
	}
    
}
