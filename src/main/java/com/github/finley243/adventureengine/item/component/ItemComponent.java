package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemComponent implements ScriptValueHolder, StatHolder {

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

    private boolean actionsRestricted() {
        return getTemplate().actionsRestricted();
    }

    public void onInit() {}

    public void onStartRound() {}

    public List<Action> getInventoryActions(ActionDependencies dependencies, Actor subject) {
        if (!actionsRestricted()) {
            return getPossibleInventoryActions(dependencies, subject);
        }
        return new ArrayList<>();
    }

    protected List<Action> getPossibleInventoryActions(ActionDependencies dependencies, Actor subject) {
        return new ArrayList<>();
    }

    @Override
    public Expression getScriptValue(String name, Context context) {
        return null;
    }

    @Override
    public boolean setScriptValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public Stat getStat(String name) {
        return null;
    }

    @Override
    public void onStatChange(String name) {

    }

}
