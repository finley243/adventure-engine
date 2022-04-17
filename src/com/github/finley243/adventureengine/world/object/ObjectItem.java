package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class ObjectItem extends WorldObject {

    private final Item item;
    private int count;

    public ObjectItem(Game game, String ID, Area area, Item item, int count) {
        super(game, ID, area, item.getName(), item.getDescription(), item.getTemplate().getScripts());
        this.item = item;
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
        // Take
        if(count > 1) {
            // Take all
        }
        return actions;
    }

}
