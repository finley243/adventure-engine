package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateSkillCheck;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentSkillCheck extends ObjectComponent {

    private final String templateID;

    private boolean hasSucceeded;

    public ObjectComponentSkillCheck(String ID, WorldObject object, String templateID) {
        super(ID, object);
        this.templateID = templateID;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateSkillCheck();
    }

    public ObjectComponentTemplateSkillCheck getTemplateSkillCheck() {
        return (ObjectComponentTemplateSkillCheck) getObject().game().data().getObjectComponentTemplate(templateID);
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        return actions;
    }

    @Override
    public boolean getValueBoolean(String name) {
        if ("succeeded".equals(name)) {
            return hasSucceeded;
        }
        return super.getValueBoolean(name);
    }

}
