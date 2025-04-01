package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptRandomValueFromCollection extends Script {

    public ScriptRandomValueFromCollection(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression collectionExpression = context.getLocalVariables().get("collection").getExpression();
        if (collectionExpression.getDataType() != Expression.DataType.LIST && collectionExpression.getDataType() != Expression.DataType.SET) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Collection parameter is not a collection", getTraceData()));
        }
        Expression selectedValue = switch (collectionExpression.getDataType()) {
            case LIST -> MathUtils.selectRandomFromList(collectionExpression.getValueList());
            case SET -> MathUtils.selectRandomFromSet(collectionExpression.getValueSet());
            default -> null;
        };
        return new ScriptReturnData(selectedValue, FlowStatementType.RETURN, null);
    }

}
