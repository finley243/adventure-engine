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
    protected void executeSuccess(Actor subject) {
        Item itemPlaceholder = ItemFactory.create(subject.game(), item);
        switch (type) {
            case "one":
                actor.getActor(subject).inventory().removeItem(itemPlaceholder);
                target.getActor(subject).inventory().addItem(itemPlaceholder);
                break;
            case "all":
                if (item == null) { // All items in inventory
                    Map<Item, Integer> items = actor.getActor(subject).inventory().getItemMap();
                    actor.getActor(subject).inventory().clear();
                    target.getActor(subject).inventory().addItems(items);
                } else { // All items of type
                    int inventoryCount = actor.getActor(subject).inventory().itemCount(itemPlaceholder);
                    actor.getActor(subject).inventory().removeItems(itemPlaceholder, inventoryCount);
                    target.getActor(subject).inventory().addItems(itemPlaceholder, inventoryCount);
                }
                break;
            case "count":
            default:
                actor.getActor(subject).inventory().removeItems(itemPlaceholder, count);
                target.getActor(subject).inventory().addItems(itemPlaceholder, count);
                break;
        }
    }

}
