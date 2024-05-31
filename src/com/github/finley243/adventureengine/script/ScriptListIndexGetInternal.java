package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptListIndexGetInternal extends Script {

    private final Script listScript;
    private final Script indexScript;

    public ScriptListIndexGetInternal(Script listScript, Script indexScript) {
        this.listScript = listScript;
        this.indexScript = indexScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData listResult = listScript.execute(context);
        if (listResult.error() != null) {
            return listResult;
        } else if (listResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, "List expression contains unexpected flow statement");
        } else if (listResult.value() == null) {
            return new ScriptReturnData(null, null, "List expression is null");
        }
        Expression listExpression = listResult.value();
        ScriptReturnData indexResult = indexScript.execute(context);
        if (indexResult.error() != null) {
            return indexResult;
        } else if (indexResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, "Index expression contains unexpected flow statement");
        } else if (indexResult.value() == null) {
            return new ScriptReturnData(null, null, "Index expression is null");
        }
        Expression indexExpression = indexResult.value();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, "List expression is not a list");
        if (indexExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, "Index expression is not an integer");
        List<Expression> list = listExpression.getValueList();
        int index = indexExpression.getValueInteger();
        if (index < 0 || index >= list.size()) return new ScriptReturnData(null, null, "Index is out of bounds");
        Expression valueAtIndex = list.get(index);
        return new ScriptReturnData(valueAtIndex, null, null);
    }

}
