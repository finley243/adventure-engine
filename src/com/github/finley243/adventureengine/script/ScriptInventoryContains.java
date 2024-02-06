package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashSet;
import java.util.Set;

public class ScriptInventoryContains extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression inventoryExpression = context.getLocalVariables().get("inventory").getExpression();
        Expression itemExpression = context.getLocalVariables().get("item").getExpression();
        Expression requireAllExpression = context.getLocalVariables().get("requireAll").getExpression();
        if (inventoryExpression.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, null, "Inventory parameter is not an inventory");
        if (itemExpression.getDataType() != Expression.DataType.STRING && itemExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, "Item parameter is not a string or set");
        if (requireAllExpression.getDataType() != Expression.DataType.BOOLEAN) return new ScriptReturnData(null, null, "RequireAll parameter is not a boolean");
        Inventory inventory = inventoryExpression.getValueInventory();
        boolean requireAll = requireAllExpression.getValueBoolean();
        if (itemExpression.getDataType() == Expression.DataType.SET) {
            Set<String> itemIDSet = new HashSet<>();
            for (Expression itemExpressionFromSet : itemExpression.getValueSet()) {
                if (itemExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "Item set contains a value that is not a string");
                itemIDSet.add(itemExpressionFromSet.getValueString());
            }
            for (String itemID : itemIDSet) {
                boolean hasItem = inventory.hasItem(itemID);
                if (hasItem != requireAll) {
                    return new ScriptReturnData(Expression.constant(hasItem), null, null);
                }
            }
            return new ScriptReturnData(Expression.constant(requireAll), null, null);
        } else {
            String itemID = itemExpression.getValueString();
            return new ScriptReturnData(Expression.constant(inventory.hasItem(itemID)), null, null);
        }
    }

}
