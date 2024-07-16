package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.textgen.LangUtils;

public class ScriptStringCase extends Script {

    public enum CaseType {
        LOWER, UPPER, TITLE
    }

    private final CaseType caseType;

    public ScriptStringCase(ScriptTraceData traceData, CaseType caseType) {
        super(traceData);
        this.caseType = caseType;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression stringExpression = context.getLocalVariables().get("string").getExpression();
        if (stringExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("String parameter is not a string", getTraceData()));
        String stringValue = stringExpression.getValueString();
        String modifiedValue = switch (caseType) {
            case LOWER -> stringValue.toLowerCase();
            case UPPER -> stringValue.toUpperCase();
            case TITLE -> LangUtils.titleCase(stringValue);
        };
        return new ScriptReturnData(Expression.constant(modifiedValue), FlowStatementType.RETURN, null);
    }

}
