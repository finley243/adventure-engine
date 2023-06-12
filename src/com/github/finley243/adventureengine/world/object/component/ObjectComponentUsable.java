package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionObjectUseStart;
import com.github.finley243.adventureengine.action.ActionObjectUseEnd;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateUsable;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentUsable extends ObjectComponent {

    private final String templateID;
    private Actor user;

    public ObjectComponentUsable(String ID, WorldObject object, String templateID) {
        super(ID, object);
        this.templateID = templateID;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateUsable();
    }

    public ObjectComponentTemplateUsable getTemplateUsable() {
        return (ObjectComponentTemplateUsable) getObject().game().data().getObjectComponentTemplate(templateID);
    }

    public Actor getUser() {
        return user;
    }

    public void setUser(Actor user) {
        this.user = user;
    }

    public void removeUser() {
        this.user = null;
    }

    @Override
    public void onSetObjectEnabled(boolean enable) {
        if (!enable && user != null) {
            user.setUsingObject(null);
            removeUser();
        }
    }

    @Override
    public void onSetObjectArea(Area area) {
        if (user != null) {
            user.setArea(area);
        }
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        if (user == null && (!subject.isUsingObject() || !subject.getUsingObject().equals(this))) {
            actions.add(new ActionObjectUseStart(this));
        }
        return actions;
    }

    public List<Action> getUsingActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionObjectUseEnd(this));
        for (String exposedComponent : getTemplateUsable().getComponentsExposed()) {
            actions.addAll(getObject().getComponent(exposedComponent).getActions(subject));
        }
        for (ActionCustom.CustomActionHolder usingAction : getTemplateUsable().getUsingActions()) {
            ActionCustom action = new ActionCustom(getObject().game(), getObject(), null, usingAction.action(), usingAction.parameters(), new String[] {getObject().getName()}, false);
            if (action.canShow(subject)) {
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    public boolean getValueBoolean(String name) {
        if ("hasUser".equals(name)) {
            return getUser() != null;
        }
        return super.getValueBoolean(name);
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        if ("user".equals(name)) {
            return getUser();
        }
        return super.getSubHolder(name, ID);
    }

}
