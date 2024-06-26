package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptListIndexSetInternal extends Script {

    private final Script listScript;
    private final Script indexScript;
    private final Script valueScript;

    public ScriptListIndexSetInternal(ScriptTraceData traceData, Script listScript, Script indexScript, Script valueScript) {
        super(traceData);
        this.listScript = listScript;
        this.indexScript = indexScript;
        this.valueScript = valueScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData listResult = listScript.execute(context);
        if (listResult.error() != null) {
            return listResult;
        } else if (listResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("List expression contains unexpected flow statement", getTraceData()));
        } else if (listResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("List expression is null", getTraceData()));
        }
        Expression listExpression = listResult.value();
        ScriptReturnData indexResult = indexScript.execute(context);
        if (indexResult.error() != null) {
            return indexResult;
        } else if (indexResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Index expression contains unexpected flow statement", getTraceData()));
        } else if (indexResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Index expression is null", getTraceData()));
        }
        Expression indexExpression = indexResult.value();
        ScriptReturnData valueResult = valueScript.execute(context);
        if (valueResult.error() != null) {
            return valueResult;
        } else if (valueResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Value expression contains unexpected flow statement", getTraceData()));
        }
        Expression valueExpression = valueResult.value();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List expression is not a list", getTraceData()));
        if (indexExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index expression is not an integer", getTraceData()));
        List<Expression> list = listExpression.getValueList();
        int index = indexExpression.getValueInteger();
        if (index < 0 || index >= list.size()) return new ScriptReturnData(null, null, new ScriptErrorData("Index is out of bounds", getTraceData()));
        list.set(index, valueExpression);
        return new ScriptReturnData(null, null, null);
    }

}
