package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionSleep;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.ComponentLink;

import java.util.List;
import java.util.Map;

public class ObjectBed extends UsableObject {

    public ObjectBed(Game game, String ID, String templateID, Area area, boolean startDisabled, boolean startHidden, Map<String, ComponentLink> linkedObjects, List<ActionCustom> customUsingActions) {
        super(game, ID, templateID, area, startDisabled, startHidden, linkedObjects, customUsingActions);
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
