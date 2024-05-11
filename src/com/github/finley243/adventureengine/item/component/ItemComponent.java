package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.stat.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemComponent implements MutableStatHolder {

    private final Item item;
    private final ItemComponentTemplate template;

    public ItemComponent(Item item, ItemComponentTemplate template) {
        this.item = item;
        this.template = template;
    }

    public abstract boolean hasState();

    public Item getItem() {
        return item;
    }

    protected ItemComponentTemplate getTemplate() {
        return template;
    }

    public boolean actionsRestricted() {
        return getTemplate().actionsRestricted();
    }

    public void onInit() {

    }

    public List<Action> inventoryActions(Actor subject) {
        return new ArrayList<>();
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return null;
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

    @Override
    public StatInt getStatInt(String name) {
        return null;
    }

    @Override
    public StatFloat getStatFloat(String name) {
        return null;
    }

    @Override
    public StatBoolean getStatBoolean(String name) {
        return null;
    }

    @Override
    public StatString getStatString(String name) {
        return null;
    }

    @Override
    public StatStringSet getStatStringSet(String name) {
        return null;
    }

    @Override
    public void onStatChange(String name) {

    }

}
