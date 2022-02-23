package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;
import java.util.Map;

public class ObjectCustom extends WorldObject {

    private final List<ActionCustom> objectActions;

    public ObjectCustom(String ID, String name, String description, Map<String, Script> scripts, List<ActionCustom> objectActions) {
        super(ID, name, description, scripts);
        this.objectActions = objectActions;
        for(ActionCustom action : objectActions) {
            action.setObject(this);
        }
    }

    @Override
    public List<Action> localActions(Actor subject) {
        List<Action> actions = super.localActions(subject);
        actions.addAll(objectActions);
        return actions;
    }

}
