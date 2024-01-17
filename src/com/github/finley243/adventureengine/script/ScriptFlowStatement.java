package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;

public class ScriptFlowStatement extends Script {

    private final FlowStatementType statementType;

    public ScriptFlowStatement(FlowStatementType statementType) {
        if (statementType == FlowStatementType.RETURN) throw new IllegalArgumentException("ScriptFlowStatement should not be used for return statements, use ScriptReturn instead");
        this.statementType = statementType;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        return new ScriptReturnData(null, statementType, null);
    }

}
