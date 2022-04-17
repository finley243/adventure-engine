package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class ObjectItem extends WorldObject {

    private final ItemStack item;

    public ObjectItem(Game game, String ID, Area area, ItemStack item) {
        super(game, ID, area, item.getItem().getName(), item.getItem().getDescription(), item.getItem().getTemplate().getScripts());
        this.item = item;
    }

    @Override
    public List<Action> localActions(Actor subject) {
        List<Action> actions = super.localActions(subject);
        //actions.add(new ActionItemTake(this));
        return actions;
    }

}
