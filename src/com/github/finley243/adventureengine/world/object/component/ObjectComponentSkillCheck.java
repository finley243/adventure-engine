package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateSkillCheck;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentSkillCheck extends ObjectComponent {

    private final ObjectComponentTemplateSkillCheck template;

    private boolean hasSucceeded;

    public ObjectComponentSkillCheck(String ID, WorldObject object, ObjectComponentTemplateSkillCheck template) {
        super(ID, object, template.startEnabled());
        this.template = template;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        return actions;
    }

}
