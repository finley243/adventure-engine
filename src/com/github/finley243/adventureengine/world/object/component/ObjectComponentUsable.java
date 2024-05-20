package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionObjectUseEnd;
import com.github.finley243.adventureengine.action.ActionObjectUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateUsable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectComponentUsable extends ObjectComponent {

    private final Map<String, Actor> users;

    public ObjectComponentUsable(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
        this.users = new HashMap<>();
    }

    private ObjectComponentTemplateUsable getTemplateUsable() {
        return (ObjectComponentTemplateUsable) getTemplate();
    }

    public String getStartPhrase(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).startPhrase();
    }

    public String getEndPhrase(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).endPhrase();
    }

    public String getStartPrompt(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).startPrompt();
    }

    public String getEndPrompt(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).endPrompt();
    }

    public boolean userIsInCover(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).userIsInCover();
    }

    public boolean userIsHidden(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).userIsHidden();
    }

    public boolean userCanSeeOtherAreas(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).userCanSeeOtherAreas();
    }

    public boolean userCanPerformLocalActions(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).userCanPerformLocalActions();
    }

    public boolean userCanPerformParentActions(String slotID) {
        return getTemplateUsable().getUsableSlotData().get(slotID).userCanPerformParentActions();
    }

    public Actor getUser(String slotID) {
        return users.get(slotID);
    }

    public void setUser(String slotID, Actor user) {
        users.put(slotID, user);
    }

    public void removeUser(String slotID) {
        users.remove(slotID);
    }

    @Override
    public void onSetObjectEnabled(boolean enable) {
        if (!enable) {
            for (String activeSlot : users.keySet()) {
                users.get(activeSlot).setUsingObject(null);
                removeUser(activeSlot);
            }
        }
    }

    @Override
    public void onSetObjectArea(Area area) {
        for (Actor user : users.values()) {
            user.setArea(area);
        }
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        for (String slotID : getTemplateUsable().getUsableSlotData().keySet()) {
            if (!users.containsKey(slotID) && (!subject.isUsingObject() || !subject.getUsingObject().equals(this))) {
                actions.add(new ActionObjectUseStart(this, slotID));
            }
        }
        return actions;
    }

    public List<Action> getUsingActions(String slotID, Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionObjectUseEnd(this, slotID));
        for (String exposedComponentName : getTemplateUsable().getUsableSlotData().get(slotID).componentsExposed()) {
            ObjectComponent component = getObject().getComponentOfType(ObjectComponentFactory.getClassFromName(exposedComponentName));
            if (component.isEnabled()) {
                actions.addAll(component.getPossibleActions(subject));
            }
        }
        for (ActionCustom.CustomActionHolder usingAction : getTemplateUsable().getUsableSlotData().get(slotID).usingActions()) {
            actions.add(new ActionCustom(getObject().game(), null, getObject(), null, null, usingAction.action(), usingAction.parameters(), new MenuDataObject(getObject()), false));
        }
        return actions;
    }

    @Override
    protected String getStatName() {
        return "usable";
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        if (name.startsWith("has_user_")) {
            for (String slotID : getTemplateUsable().getUsableSlotData().keySet()) {
                if (name.equals("has_user_" + slotID)) {
                    return Expression.constant(getUser(slotID) != null);
                }
            }
            return null;
        }
        return super.getStatValue(name, context);
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        if ("user".equals(name)) {
            return getUser(ID);
        }
        return super.getSubHolder(name, ID);
    }

    public record ObjectUserData(WorldObject object, String slot) {}

}
