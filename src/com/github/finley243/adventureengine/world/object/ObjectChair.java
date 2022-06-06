package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;
import java.util.Map;

public class ObjectChair extends UsableObject {

    public ObjectChair(Game game, String ID, Area area, String name, Scene description, boolean startDisabled, boolean startHidden, Map<String, Script> scripts, List<ActionCustom> customActions, List<ActionCustom> customUsingActions) {
        super(game, ID, area, name, description, startDisabled, startHidden, scripts, customActions, customUsingActions);
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
