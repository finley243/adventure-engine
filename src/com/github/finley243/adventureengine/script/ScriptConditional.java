package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptConditional extends Script {

    private final List<ConditionalScriptPair> conditionalScriptPairs;
    private final Script scriptElse;

    public ScriptConditional(ScriptTraceData traceData, List<ConditionalScriptPair> conditionalScriptPairs, Script scriptElse) {
        super(traceData);
        this.conditionalScriptPairs = conditionalScriptPairs;
        this.scriptElse = scriptElse;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        for (ConditionalScriptPair scriptPair : conditionalScriptPairs) {
            ScriptReturnData conditionResult = scriptPair.condition.execute(context);
            if (conditionResult.error() != null) {
                return conditionResult;
            } else if (conditionResult.flowStatement() != null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getTraceData()));
            } else if (conditionResult.value() == null) {
                return new ScriptReturnData(null, null, new ScriptErrorData("Expression did not return a value", getTraceData()));
            } else if (conditionResult.value().getDataType() != Expression.DataType.BOOLEAN) {
                return new ScriptReturnData(null, null, new ScriptErrorData("Expression did not return a boolean value", getTraceData()));
            }
            if (conditionResult.value().getValueBoolean()) {
                return scriptPair.script.execute(context);
            }
        }
        if (scriptElse != null) {
            return scriptElse.execute(context);
        }
        return new ScriptReturnData(null, null, null);
    }

    public static class ConditionalScriptPair {
        private final Script condition;
        private final Script script;

        public ConditionalScriptPair(Script condition, Script script) {
            this.condition = condition;
            this.script = script;
        }
    }

}
