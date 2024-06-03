package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptListIndexGetInternal extends Script {

    private final Script listScript;
    private final Script indexScript;

    public ScriptListIndexGetInternal(int line, Script listScript, Script indexScript) {
        super(line);
        this.listScript = listScript;
        this.indexScript = indexScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData listResult = listScript.execute(context);
        if (listResult.error() != null) {
            return listResult;
        } else if (listResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("List expression contains unexpected flow statement", getLine()));
        } else if (listResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("List expression is null", getLine()));
        }
        Expression listExpression = listResult.value();
        ScriptReturnData indexResult = indexScript.execute(context);
        if (indexResult.error() != null) {
            return indexResult;
        } else if (indexResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Index expression contains unexpected flow statement", getLine()));
        } else if (indexResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Index expression is null", getLine()));
        }
        Expression indexExpression = indexResult.value();
        if (listExpression.getDataType() != Expression.DataType.LIST) return new ScriptReturnData(null, null, new ScriptErrorData("List expression is not a list", getLine()));
        if (indexExpression.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Index expression is not an integer", getLine()));
        List<Expression> list = listExpression.getValueList();
        int index = indexExpression.getValueInteger();
        if (index < 0 || index >= list.size()) return new ScriptReturnData(null, null, new ScriptErrorData("Index is out of bounds", getLine()));
        Expression valueAtIndex = list.get(index);
        return new ScriptReturnData(valueAtIndex, null, null);
    }

}
