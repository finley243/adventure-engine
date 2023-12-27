package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.QueuedEvent;
import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;

import java.util.*;

public class ScriptInventoryIterator extends Script implements ScriptReturnTarget {

    private final Expression inventoryExpression;
    private final Script iteratedScript;

    // TODO - Fix for recursive functions (values will be overwritten)
    private final Deque<Map.Entry<Item, Integer>> itemQueue;
    private Context context;

    public ScriptInventoryIterator(Condition condition, Expression inventoryExpression, Script iteratedScript) {
        super(condition);
        this.inventoryExpression = inventoryExpression;
        this.iteratedScript = iteratedScript;
        this.itemQueue = new ArrayDeque<>();
    }

    @Override
    protected void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        if (inventoryExpression.getDataType(context) != Expression.DataType.INVENTORY) throw new IllegalArgumentException("ScriptInventoryIterator inventory expression is not an inventory");
        this.context = context;
        itemQueue.clear();
        Inventory inventory = inventoryExpression.getValueInventory(context);
        Map<Item, Integer> itemMap = inventory.getItemMap();
        itemQueue.addAll(itemMap.entrySet());
        executeNextIteration();
        /*List<QueuedEvent> scriptEvents = new ArrayList<>();
        for (Map.Entry<Item, Integer> itemEntry : itemMap.entrySet()) {
            Context innerContext = new Context(context);
            scriptEvents.add(new ScriptEvent(iteratedScript, new Context(innerContext, new MapBuilder<String, Expression>().put("count", Expression.constant(itemEntry.getValue())).build(), itemEntry.getKey())));
        }
        context.game().eventQueue().addAllToFront(scriptEvents);*/
    }

    private void executeNextIteration() {
        Map.Entry<Item, Integer> currentItem = itemQueue.removeFirst();
        Context innerContext = new Context(context, new MapBuilder<String, Expression>().put("count", Expression.constant(currentItem.getValue())).build(), currentItem.getKey());
        iteratedScript.execute(innerContext, this);
    }

    @Override
    public void onScriptReturn(ScriptReturn scriptReturn) {
        if (scriptReturn.error() != null) {
            sendReturn(scriptReturn);
        } else if (scriptReturn.isReturn()) {
            sendReturn(scriptReturn);
        } else if (itemQueue.isEmpty()) {
            sendReturn(new ScriptReturn(null, false, false, null));
        } else {
            executeNextIteration();
        }
    }

}
