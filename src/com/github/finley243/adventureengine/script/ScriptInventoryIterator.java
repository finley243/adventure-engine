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

    public ScriptInventoryIterator(ScriptTraceData traceData, Script inventoryExpression, Script iteratedScript) {
        super(traceData);
        this.inventoryExpression = inventoryExpression;
        this.iteratedScript = iteratedScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData setResult = inventoryExpression.execute(context);
        if (setResult.error() != null) {
            return setResult;
        } else if (setResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        } else if (setResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression did not receive a value", getTraceData()));
        } else if (setResult.value().getDataType() != Expression.DataType.INVENTORY) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression expected an inventory", getTraceData()));
        }
        Inventory inventory = setResult.value().getValueInventory();
        for (Map.Entry<Item, Integer> currentItem : inventory.getItemMap().entrySet()) {
            Context innerContext = new Context(context, new MapBuilder<String, Expression>().put("count", Expression.constant(currentItem.getValue())).build(), currentItem.getKey());
            ScriptReturnData scriptResult = iteratedScript.execute(innerContext);
            if (scriptResult.error() != null) {
                return scriptResult;
            } else if (scriptResult.flowStatement() == FlowStatementType.RETURN) {
                return scriptResult;
            } else if (scriptResult.flowStatement() == FlowStatementType.BREAK) {
                return new ScriptReturnData(null, null, null);
            }
            // Continue statement is handled implicitly
        }
        return new ScriptReturnData(null, null, null);
    }

}
