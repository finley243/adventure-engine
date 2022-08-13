package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;

import java.util.Map;

public class ScriptTransferItem extends Script {

    private final ActorReference actor;
    private final ActorReference target;
    private final String item;
    private final String type;
    private final int count;

    public ScriptTransferItem(Condition condition, ActorReference actor, ActorReference target, String item, String type, int count) {
        super(condition);
        this.actor = actor;
        this.target = target;
        this.item = item;
        this.type = type;
        this.count = count;
    }

    @Override
    protected void executeSuccess(Actor subject, Actor target) {
        Item itemPlaceholder = ItemFactory.create(subject.game(), item);
        switch (type) {
            case "one":
                actor.getActor(subject, target).inventory().removeItem(itemPlaceholder);
                this.target.getActor(subject, target).inventory().addItem(itemPlaceholder);
                break;
            case "all":
                if (item == null) { // All items in inventory
                    Map<Item, Integer> items = actor.getActor(subject, target).inventory().getItemMap();
                    actor.getActor(subject, target).inventory().clear();
                    this.target.getActor(subject, target).inventory().addItems(items);
                } else { // All items of type
                    int inventoryCount = actor.getActor(subject, target).inventory().itemCount(itemPlaceholder);
                    actor.getActor(subject, target).inventory().removeItems(itemPlaceholder, inventoryCount);
                    this.target.getActor(subject, target).inventory().addItems(itemPlaceholder, inventoryCount);
                }
                break;
            case "count":
            default:
                actor.getActor(subject, target).inventory().removeItems(itemPlaceholder, count);
                this.target.getActor(subject, target).inventory().addItems(itemPlaceholder, count);
                break;
        }
    }

}
