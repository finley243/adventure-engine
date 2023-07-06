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
    private final String ID;
    private final WorldObject object;
    private final ObjectComponentTemplate template;

    public ObjectComponent(String ID, WorldObject object, ObjectComponentTemplate template) {
        if (ID == null) throw new IllegalArgumentException("ObjectComponent ID cannot be null");
        if (ID.isEmpty()) throw new IllegalArgumentException("ObjectComponent ID cannot be empty");
        this.ID = ID;
        this.object = object;
        this.template = template;
    }

    protected ObjectComponentTemplate getTemplate() {
        return template;
    }

    public abstract List<Action> getActions(Actor subject);

    public String getName() {
        return getTemplate().getName();
    }

    public boolean actionsRestricted() {
        return getTemplate().actionsRestricted();
    }

    public String getID() {
        return ID;
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
            case "id" -> new ExpressionConstantString(getID());
            default -> null;
        };
    }

    @Override
    public void setStateBoolean(String name, boolean value) {
        if ("enabled".equals(name)) {
            setEnabled(value);
        }
    }

    @Override
    public void setStateInteger(String name, int value) {

    }

    @Override
    public void setStateFloat(String name, float value) {

    }

    @Override
    public void setStateString(String name, String value) {

    }

    @Override
    public void setStateStringSet(String name, Set<String> value) {

    }

    @Override
    public void modStateInteger(String name, int amount) {

    }

    @Override
    public void modStateFloat(String name, float amount) {

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
            return component.getObject().equals(this.getObject()) && component.getID().equals(this.getID());
        }
    }

}
