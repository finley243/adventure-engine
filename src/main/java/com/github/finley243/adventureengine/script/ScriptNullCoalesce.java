package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptNullCoalesce extends Script {

    private final Script evaluatedExpression;
    private final Script nullExpression;

    public ScriptNullCoalesce(ScriptTraceData traceData, Script evaluatedExpression, Script nullExpression) {
        super(traceData);
        this.evaluatedExpression = evaluatedExpression;
        this.nullExpression = nullExpression;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        ScriptReturnData evaluatedReturn = evaluatedExpression.execute(scriptRuntime, context);
        if (evaluatedReturn.error() != null) {
            return evaluatedReturn;
        } else if (evaluatedReturn.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        Expression evaluatedValue = evaluatedReturn.value();
        if (evaluatedValue != null) {
            return new ScriptReturnData(evaluatedValue, null, null);
        }
        ScriptReturnData nullReturn = nullExpression.execute(scriptRuntime, context);
        if (nullReturn.error() != null) {
            return nullReturn;
        } else if (nullReturn.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
        }
        Expression nullValue = nullReturn.value();
        return new ScriptReturnData(nullValue, null, null);
    }

}
