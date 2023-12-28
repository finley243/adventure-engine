package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;

import java.util.*;

public class ScriptInventoryIterator extends Script implements ScriptReturnTarget {

    private final Expression inventoryExpression;
    private final Script iteratedScript;

    public ScriptInventoryIterator(Condition condition, Expression inventoryExpression, Script iteratedScript) {
        super(condition);
        this.inventoryExpression = inventoryExpression;
        this.iteratedScript = iteratedScript;
    }

    @Override
    protected void executeSuccess(RuntimeStack runtimeStack) {
        if (inventoryExpression.getDataType(runtimeStack.getContext()) != Expression.DataType.INVENTORY) throw new IllegalArgumentException("ScriptInventoryIterator inventory expression is not an inventory");
        Inventory inventory = inventoryExpression.getValueInventory(runtimeStack.getContext());
        Map<Item, Integer> itemMap = inventory.getItemMap();
        runtimeStack.addContextItemIterator(runtimeStack.getContext(), null, itemMap.entrySet());
        executeNextIteration(runtimeStack);
    }

    private void executeNextIteration(RuntimeStack runtimeStack) {
        Map.Entry<Item, Integer> currentItem = runtimeStack.removeQueuedItem();
        Context innerContext = new Context(runtimeStack.getContext(), new MapBuilder<String, Expression>().put("count", Expression.constant(currentItem.getValue())).build(), currentItem.getKey());
        runtimeStack.addContext(innerContext, this);
        iteratedScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturn scriptReturn) {
        runtimeStack.closeContext();
        if (scriptReturn.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (scriptReturn.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (runtimeStack.itemQueueIsEmpty()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
        } else {
            executeNextIteration(runtimeStack);
        }
    }

}
