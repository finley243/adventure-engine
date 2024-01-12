package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptModifyGlobal extends Script {

    private final Expression globalID;
    private final Expression expression;

    public ScriptModifyGlobal(Condition condition, Expression globalID, Expression expression) {
        super(condition);
        if (globalID == null) throw new IllegalArgumentException("ScriptModifyGlobal globalID is null");
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (globalID.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptModifyGlobal globalID is not a string");
        String globalIDValue = globalID.getValueString(context);
        if (context.game().data().getGlobalExpression(globalIDValue).getDataType(context) != expression.getDataType(context)) throw new IllegalArgumentException("ScriptModifyGlobal expression data type does not match global");
        switch (expression.getDataType(context)) {
            case INTEGER -> {
                int oldValueInt = context.game().data().getGlobalExpression(globalIDValue).getValueInteger(context);
                context.game().data().setGlobalExpression(globalIDValue, Expression.constant(oldValueInt + expression.getValueInteger(context)));
            }
            case FLOAT -> {
                float oldValueFloat = context.game().data().getGlobalExpression(globalIDValue).getValueFloat(context);
                context.game().data().setGlobalExpression(globalIDValue, Expression.constant(oldValueFloat + expression.getValueFloat(context)));
            }
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + expression.getDataType(context));
        }
        sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
    }

}
