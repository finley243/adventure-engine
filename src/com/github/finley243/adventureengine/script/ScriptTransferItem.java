package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
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
    protected void executeSuccess(ContextScript context) {
        Item itemPlaceholder = ItemFactory.create(context.game(), item);
        switch (type) {
            case "one":
                actor.getActor(context).getInventory().removeItem(itemPlaceholder);
                target.getActor(context).getInventory().addItem(itemPlaceholder);
                break;
            case "all":
                if (item == null) { // All items in inventory
                    Map<Item, Integer> items = actor.getActor(context).getInventory().getItemMap();
                    actor.getActor(context).getInventory().clear();
                    target.getActor(context).getInventory().addItems(items);
                } else { // All items of type
                    int inventoryCount = actor.getActor(context).getInventory().itemCount(itemPlaceholder);
                    actor.getActor(context).getInventory().removeItems(itemPlaceholder, inventoryCount);
                    target.getActor(context).getInventory().addItems(itemPlaceholder, inventoryCount);
                }
                break;
            case "count":
            default:
                actor.getActor(context).getInventory().removeItems(itemPlaceholder, count);
                target.getActor(context).getInventory().addItems(itemPlaceholder, count);
                break;
        }
    }

}
