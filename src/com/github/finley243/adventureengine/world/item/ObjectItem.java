package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemTake;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class ObjectItem extends WorldObject {

    private final ItemTemplate item;
    private int count;
    private final Item state;

    public ObjectItem(Game game, String ID, Area area, ItemTemplate item, int count, Item state) {
        super(game, ID, area, item.getName(), item.getDescription(), item.getScripts());
        this.item = item;
        this.count = count;
        this.state = state;
    }

    @Override
    public List<Action> localActions(Actor subject) {
        List<Action> actions = super.localActions(subject);
        //actions.add(new ActionItemTake(this));
        return actions;
    }

}
