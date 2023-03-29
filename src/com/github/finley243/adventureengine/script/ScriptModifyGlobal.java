package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

public class ScriptModifyGlobal extends Script {

    private final String globalID;
    private final Variable variable;

    public ScriptModifyGlobal(Condition condition, String globalID, Variable variable) {
        super(condition);
        this.globalID = globalID;
        this.variable = variable;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        switch (variable.getDataType()) {
            case INTEGER -> {
                int oldValueInt = context.game().data().getGlobalInteger(globalID);
                context.game().data().setGlobalInteger(globalID, oldValueInt + variable.getValueInteger(context));
            }
            case FLOAT -> {
                float oldValueFloat = context.game().data().getGlobalFloat(globalID);
                context.game().data().setGlobalFloat(globalID, oldValueFloat + variable.getValueFloat(context));
            }
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + variable.getDataType());
        }
    }

}
