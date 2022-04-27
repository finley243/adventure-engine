package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;

public class ScriptTransferItem extends Script {

    private final ActorReference actor;
    private final ActorReference target;
    private final String item;

    public ScriptTransferItem(Condition condition, ActorReference actor, ActorReference target, String item) {
        super(condition);
        this.actor = actor;
        this.target = target;
        this.item = item;
    }

    @Override
    protected void executeSuccess(Actor subject) {
        Item itemPlaceholder = ItemFactory.create(subject.game(), item);
        if (actor.getActor(subject).inventory().hasItem(item)) {
            actor.getActor(subject).inventory().removeItem(itemPlaceholder);
            target.getActor(subject).inventory().addItem(itemPlaceholder);
        }
    }

}
