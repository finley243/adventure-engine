package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
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

    private boolean actionsRestricted() {
        return getTemplate().actionsRestricted();
    }

    public void onInit(Game game) {}

    public void onStartRound(Game game) {}

    public List<Action> getInventoryActions(Game game, Actor subject) {
        if (!actionsRestricted()) {
            return getPossibleInventoryActions(game, subject);
        }
        return new ArrayList<>();
    }

    protected List<Action> getPossibleInventoryActions(Game game, Actor subject) {
        return new ArrayList<>();
    }

    @Override
    public Expression getStatValue(String name, Context context, Game game) {
        return null;
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context, Game game) {
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

    @Override
    public StatInt getStatInt(Game game, String name) {
        return null;
    }

    @Override
    public StatFloat getStatFloat(Game game, String name) {
        return null;
    }

    @Override
    public StatBoolean getStatBoolean(Game game, String name) {
        return null;
    }

    @Override
    public StatString getStatString(Game game, String name) {
        return null;
    }

    @Override
    public StatStringSet getStatStringSet(Game game, String name) {
        return null;
    }

    @Override
    public void onStatChange(Game game, String name) {

    }

}
