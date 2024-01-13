package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;

import java.util.Map;

public class ScriptInventoryIterator extends Script {

    private final Script inventoryExpression;
    private final Script iteratedScript;

    public ScriptInventoryIterator(Script inventoryExpression, Script iteratedScript) {
        this.inventoryExpression = inventoryExpression;
        this.iteratedScript = iteratedScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData setResult = inventoryExpression.execute(context);
        if (setResult.error() != null) {
            return setResult;
        } else if (setResult.isReturn()) {
            return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
        } else if (setResult.value() == null) {
            return new ScriptReturnData(null, false, false, "Expression did not receive a value");
        } else if (setResult.value().getDataType(context) != Expression.DataType.INVENTORY) {
            return new ScriptReturnData(null, false, false, "Expression expected an inventory");
        }
        Inventory inventory = setResult.value().getValueInventory(context);
        for (Map.Entry<Item, Integer> currentItem : inventory.getItemMap().entrySet()) {
            Context innerContext = new Context(context, new MapBuilder<String, Expression>().put("count", Expression.constant(currentItem.getValue())).build(), currentItem.getKey());
            ScriptReturnData scriptResult = iteratedScript.execute(innerContext);
            if (scriptResult.error() != null) {
                return scriptResult;
            } else if (scriptResult.isReturn()) {
                return scriptResult;
            }
        }
        return new ScriptReturnData(null, false, false, null);
    }

}
