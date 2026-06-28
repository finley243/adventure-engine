package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptContextHolder extends Script {

    private final String name;

    public ScriptContextHolder(ScriptTraceData traceData, String name) {
        super(traceData);
        this.name = name;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        return switch (name) {
            case "subject" -> new ScriptReturnData(Expression.valueHolder(context.getSubject()), null, null);
            case "target" -> new ScriptReturnData(Expression.valueHolder(context.getTarget()), null, null);
            case "parentArea" -> new ScriptReturnData(Expression.valueHolder(context.getParentArea()), null, null);
            case "parentItem" -> new ScriptReturnData(Expression.valueHolder(context.getParentItem()), null, null);
            case "parentObject" -> new ScriptReturnData(Expression.valueHolder(context.getParentObject()), null, null);
            default -> new ScriptReturnData(null, null, new ScriptErrorData("Invalid context type: " + name, getTraceData()));
        };
    }

}
