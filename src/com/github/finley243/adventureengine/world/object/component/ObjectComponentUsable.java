package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionObjectUseEnd;
import com.github.finley243.adventureengine.action.ActionObjectUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantBoolean;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateUsable;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentUsable extends ObjectComponent {

    private Actor user;

    public ObjectComponentUsable(String ID, WorldObject object, ObjectComponentTemplate template) {
        super(ID, object, template);
    }

    private ObjectComponentTemplateUsable getTemplateUsable() {
        return (ObjectComponentTemplateUsable) getTemplate();
    }

    public String getStartPhrase() {
        return getTemplateUsable().getStartPhrase();
    }

    public String getEndPhrase() {
        return getTemplateUsable().getEndPhrase();
    }

    public String getStartPrompt() {
        return getTemplateUsable().getStartPrompt();
    }

    public String getEndPrompt() {
        return getTemplateUsable().getEndPrompt();
    }

    public boolean userIsInCover() {
        return getTemplateUsable().userIsInCover();
    }

    public boolean userIsHidden() {
        return getTemplateUsable().userIsHidden();
    }

    public boolean userCanSeeOtherAreas() {
        return getTemplateUsable().userCanSeeOtherAreas();
    }

    public boolean userCanPerformLocalActions() {
        return getTemplateUsable().userCanPerformLocalActions();
    }

    public boolean userCanPerformParentActions() {
        return getTemplateUsable().userCanPerformParentActions();
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
        for (String exposedComponentID : getTemplateUsable().getComponentsExposed()) {
            ObjectComponent component = getObject().getComponent(exposedComponentID);
            if (component.isEnabled()) {
                actions.addAll(component.getActions(subject));
            }
        }
        for (ActionCustom.CustomActionHolder usingAction : getTemplateUsable().getUsingActions()) {
            String[] menuPath;
            if (getTemplate().getName() != null) {
                menuPath = new String[] {LangUtils.titleCase(getObject().getName()), LangUtils.titleCase(getTemplate().getName())};
            } else {
                menuPath = new String[] {LangUtils.titleCase(getObject().getName())};
            }
            ActionCustom action = new ActionCustom(getObject().game(), getObject(), null, null, usingAction.action(), usingAction.parameters(), menuPath, false);
            if (action.canShow(subject)) {
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        if ("has_user".equals(name)) {
            return new ExpressionConstantBoolean(getUser() != null);
        }
        return super.getStatValue(name, context);
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        if ("user".equals(name)) {
            return getUser();
        }
        return super.getSubHolder(name, ID);
    }

}
