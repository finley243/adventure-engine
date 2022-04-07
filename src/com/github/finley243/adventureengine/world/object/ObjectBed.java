package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionSleep;
import com.github.finley243.adventureengine.action.ActionUseStop;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectBed extends UsableObject {

    public ObjectBed(Game game, String ID, Area area, String name, String description, Map<String, Script> scripts) {
        super(game, ID, area, name, description, scripts);
    }

    @Override
    public String getStartPhrase() {
        return "lieDown";
    }

    @Override
    public String getStopPhrase() {
        return "getUp";
    }

    @Override
    public String getStartPrompt() {
        return "Lie down";
    }

    @Override
    public String getStopPrompt() {
        return "Get up";
    }

    @Override
    public List<Action> usingActions() {
        List<Action> actions = super.usingActions();
        actions.add(new ActionSleep());
        return actions;
    }

}
