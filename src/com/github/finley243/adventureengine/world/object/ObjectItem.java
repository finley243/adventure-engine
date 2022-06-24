package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemTake;
import com.github.finley243.adventureengine.action.ActionItemTakeAll;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ObjectItem extends WorldObject {

    private final Item item;
    private int count;

    public ObjectItem(Game game, String ID, Area area, Item item, int count) {
        // TODO - Add startDisabled and startHidden functionality
        super(game, ID, area, item.getName(), item.getDescription(), false, false, item.getTemplate().getScripts(), new ArrayList<>());
        this.item = item;
        this.count = count;
    }

    @Override
    public boolean isKnown() {
        return item.isKnown();
    }

    @Override
    public void setKnown() {
        item.setKnown();
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int amount) {
        count += amount;
    }

    @Override
    public List<Action> localActions(Actor subject) {
        List<Action> actions = super.localActions(subject);
        actions.add(new ActionItemTake(this));
        if(count > 1) {
            actions.add(new ActionItemTakeAll(this));
        }
        return actions;
    }

}
