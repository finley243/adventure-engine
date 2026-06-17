package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectComponent implements ScriptValueHolder {

    private boolean isEnabled;
    private final WorldObject object;
    private final ObjectComponentTemplate template;

    ObjectComponent(WorldObject object, ObjectComponentTemplate template) {
        this.object = object;
        this.template = template;
        this.isEnabled = getTemplate().startEnabled();
    }

    protected ObjectComponentTemplate getTemplate() {
        return template;
    }

    public List<Action> getActions(Actor subject, ActionDependencies dependencies) {
        if (isEnabled && !actionsRestricted()) {
            return getPossibleActions(subject, dependencies);
        }
        return new ArrayList<>();
    }

    protected abstract List<Action> getPossibleActions(Actor subject, ActionDependencies dependencies);

    private boolean actionsRestricted() {
        return getTemplate().actionsRestricted();
    }

    public WorldObject getObject() {
        return object;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void onSetObjectEnabled(boolean enable) {}

    public void onSetObjectArea(Area area) {}

    protected abstract String getStatName();

    @Override
    public Expression getScriptValue(String name, Context context) {
        if ((getStatName() + "_enabled").equals(name)) {
            return Expression.bool(isEnabled());
        }
        return null;
    }

    @Override
    public boolean setScriptValue(String name, Expression value, Context context) {
        if ((getStatName() + "_enabled").equals(name)) {
            setEnabled(value.getValueBoolean());
        }
        return false;
    }

    @Override
    public ScriptValueHolder getSubHolder(String name, String ID) {
        if ("object".equals(name)) {
            return object;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjectComponent component)) {
            return false;
        } else if (!(component.getClass().equals(this.getClass()))) {
            return false;
        } else {
            return component.getObject().equals(this.getObject());
        }
    }

}
