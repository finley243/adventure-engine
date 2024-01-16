package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Set;

public class ScriptInventoryContains extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression inventoryExpression = context.getLocalVariables().get("inventory").getExpression();
        Expression itemExpression = context.getLocalVariables().get("item").getExpression();
        Expression requireAllExpression = context.getLocalVariables().get("requireAll").getExpression();
        if (inventoryExpression.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, false, false, "Inventory parameter is not an inventory");
        if (itemExpression.getDataType() != Expression.DataType.STRING && itemExpression.getDataType() != Expression.DataType.STRING_SET) return new ScriptReturnData(null, false, false, "Item parameter is not a string or set");
        if (requireAllExpression.getDataType() != Expression.DataType.BOOLEAN) return new ScriptReturnData(null, false, false, "RequireAll parameter is not a boolean");
        Inventory inventory = inventoryExpression.getValueInventory();
        boolean requireAll = requireAllExpression.getValueBoolean();
        if (itemExpression.getDataType() == Expression.DataType.STRING_SET) {
            Set<String> itemIDSet = itemExpression.getValueStringSet();
            for (String itemID : itemIDSet) {
                boolean hasItem = inventory.hasItem(itemID);
                if (hasItem != requireAll) {
                    return new ScriptReturnData(Expression.constant(hasItem), false, false, null);
                }
            }
            return new ScriptReturnData(Expression.constant(requireAll), false, false, null);
        } else {
            String itemID = itemExpression.getValueString();
            return new ScriptReturnData(Expression.constant(inventory.hasItem(itemID)), false, false, null);
        }
    }

}
