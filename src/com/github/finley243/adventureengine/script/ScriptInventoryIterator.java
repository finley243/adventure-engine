package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.QueuedEvent;
import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptInventoryIterator extends Script {

    private final Expression inventoryExpression;
    private final Script iteratedScript;

    public ScriptInventoryIterator(Condition condition, Expression inventoryExpression, Script iteratedScript) {
        super(condition);
        this.inventoryExpression = inventoryExpression;
        this.iteratedScript = iteratedScript;
    }

    @Override
    protected void executeSuccess(Context context) {
        if (inventoryExpression.getDataType(context) != Expression.DataType.INVENTORY) throw new IllegalArgumentException("ScriptInventoryIterator inventory expression is not an inventory");
        Inventory inventory = inventoryExpression.getValueInventory(context);
        Map<Item, Integer> itemMap = inventory.getItemMap();
        List<QueuedEvent> scriptEvents = new ArrayList<>();
        for (Map.Entry<Item, Integer> itemEntry : itemMap.entrySet()) {
            Context innerContext = new Context(context);
            scriptEvents.add(new ScriptEvent(iteratedScript, new Context(innerContext, new MapBuilder<String, Expression>().put("count", Expression.constant(itemEntry.getValue())).build(), itemEntry.getKey())));
        }
        context.game().eventQueue().addAllToFront(scriptEvents);
    }

}
