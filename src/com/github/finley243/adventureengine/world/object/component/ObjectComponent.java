package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantBoolean;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateInventory;

import java.util.List;
import java.util.Set;

public abstract class ObjectComponent implements StatHolder {

    private boolean isEnabled;
    private final WorldObject object;
    private final ObjectComponentTemplate template;

    public ObjectComponent(WorldObject object, ObjectComponentTemplate template) {
        this.object = object;
        this.template = template;
    }

    protected ObjectComponentTemplate getTemplate() {
        return template;
    }

    public abstract List<Action> getActions(Actor subject);

    public boolean actionsRestricted() {
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

    public void onNewGameInit() {
        this.isEnabled = getTemplate().startEnabled();
    }

    public void onSetObjectEnabled(boolean enable) {}

    public void onSetObjectArea(Area area) {}

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "enabled" -> new ExpressionConstantBoolean(isEnabled());
            default -> null;
        };
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
        switch (name) {
            case "enabled" -> {
                setEnabled(value.getValueBoolean(context));
                return true;
            }
        }
        return false;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
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
