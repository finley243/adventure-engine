package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionObjectKey;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectComponentKey extends ObjectComponent {

    private final String templateID;

    private boolean hasSucceeded;

    public ObjectComponentKey(String ID, WorldObject object, String templateID) {
        super(ID, object);
        this.templateID = templateID;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateKey();
    }

    public ObjectComponentTemplateKey getTemplateKey() {
        return (ObjectComponentTemplateKey) getObject().game().data().getObjectComponentTemplate(templateID);
    }

    public Set<String> getKeyItems() {
        return (Set<String>) getObject().getComponentParams(getID()).getParameter("keyItems");
    }

    public boolean hasSucceeded() {
        return hasSucceeded;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionObjectKey(this));
        return actions;
    }

    @Override
    public boolean getValueBoolean(String name) {
        if ("succeeded".equals(name)) {
            return hasSucceeded;
        }
        return super.getValueBoolean(name);
    }

    @Override
    public void setStateBoolean(String name, boolean value) {
        if ("succeeded".equals(name)) {
            this.hasSucceeded = value;
        } else {
            super.setStateBoolean(name, value);
        }
    }

}
