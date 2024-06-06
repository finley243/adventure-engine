package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Collection;

public class ScriptCollectionContains extends Script {

    public ScriptCollectionContains(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression collectionExpression = context.getLocalVariables().get("collection").getExpression();
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        Collection<Expression> collection = switch (collectionExpression.getDataType()) {
            case LIST -> collectionExpression.getValueList();
            case SET -> collectionExpression.getValueSet();
            default -> null;
        };
        if (collection == null) return new ScriptReturnData(null, null, new ScriptErrorData("Collection parameter is not a collection", getTraceData()));
        boolean listContainsValue = collection.contains(valueExpression);
        return new ScriptReturnData(Expression.constant(listContainsValue), FlowStatementType.RETURN, null);
    }

}
