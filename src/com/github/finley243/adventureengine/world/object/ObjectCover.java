package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;
import java.util.Map;

public class ObjectCover extends UsableObject {

    public ObjectCover(Game game, String ID, String templateID, Area area, boolean startDisabled, boolean startHidden, Map<String, String> linkedObjects, List<ActionCustom> customUsingActions) {
        super(game, ID, templateID, area, startDisabled, startHidden, linkedObjects, customUsingActions);
    }

    @Override
    public boolean userInCover() {
        return true;
    }

    @Override
    public boolean userCanSeeOtherAreas() {
        return false;
    }

    @Override
    public String getStartPhrase() {
        return "takeCover";
    }

    @Override
    public String getStopPhrase() {
        return "leaveCover";
    }

    @Override
    public String getStartPrompt() {
        return "Take cover";
    }

    @Override
    public String getStopPrompt() {
        return "Leave cover";
    }

}
