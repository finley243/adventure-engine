package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.item.Item;

import java.util.Map;

public class ScriptInventoryIterator extends Script {

    private final Expression inventoryExpression;
    private final Script iteratedScript;

    public ScriptInventoryIterator(Condition condition, Map<String, Expression> localParameters, Expression inventoryExpression, Script iteratedScript) {
        super(condition, localParameters);
        if (inventoryExpression.getDataType() != Expression.DataType.INVENTORY) throw new IllegalArgumentException("ScriptInventoryIterator inventory expression is not an inventory");
        this.inventoryExpression = inventoryExpression;
        this.iteratedScript = iteratedScript;
    }

    @Override
    protected void executeSuccess(Context context) {
        Inventory inventory = inventoryExpression.getValueInventory(context);
        Map<Item, Integer> itemMap = inventory.getItemMap();
        for (Map.Entry<Item, Integer> itemEntry : itemMap.entrySet()) {
            iteratedScript.execute(new Context(context, new MapBuilder<String, Expression>().put("count", Expression.constant(itemEntry.getValue())).build(), itemEntry.getKey()));
        }
    }

}
