package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.Map;

public class ObjectCover extends UsableObject {

    public ObjectCover(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts) {
        super(game, ID, area, name, description, scripts);
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
