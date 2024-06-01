package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScriptIterator extends Script {

    private final Script setExpression;
    private final String iteratorParameterName;
    private final Script iteratedScript;

    public ScriptIterator(Script setExpression, String iteratorParameterName, Script iteratedScript) {
        this.setExpression = setExpression;
        this.iteratorParameterName = iteratorParameterName;
        this.iteratedScript = iteratedScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData setResult = setExpression.execute(context);
        if (setResult.error() != null) {
            return setResult;
        } else if (setResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", -1));
        } else if (setResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression did not receive a value", -1));
        } else if (setResult.value().getDataType() != Expression.DataType.SET && setResult.value().getDataType() != Expression.DataType.LIST) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression expected a set or list", -1));
        }
        List<Expression> expressions;
        if (setResult.value().getDataType() == Expression.DataType.SET) {
            expressions = new ArrayList<>(setResult.value().getValueSet());
        } else {
            expressions = setResult.value().getValueList();
        }
        for (Expression currentExpression : expressions) {
            Context innerContext = new Context(context, new MapBuilder<String, Expression>().put(iteratorParameterName, currentExpression).build());
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
