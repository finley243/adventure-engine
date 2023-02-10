package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

public class ScriptSetGlobal extends Script {

    private final String globalID;
    private final Variable variable;

    public ScriptSetGlobal(Condition condition, String globalID, Variable variable) {
        super(condition);
        this.globalID = globalID;
        this.variable = variable;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        switch (variable.getDataType()) {
            case BOOLEAN:
                context.game().data().setGlobalBoolean(globalID, variable.getValueBoolean(context));
                break;
            case INTEGER:
                context.game().data().setGlobalInteger(globalID, variable.getValueInteger(context));
                break;
            case FLOAT:
                context.game().data().setGlobalFloat(globalID, variable.getValueFloat(context));
                break;
            case STRING:
                context.game().data().setGlobalString(globalID, variable.getValueString(context));
                break;
            case STRING_SET:
                context.game().data().setGlobalStringSet(globalID, variable.getValueStringSet(context));
                break;
            default:
                throw new UnsupportedOperationException("No globals for provided data type: " + variable.getDataType());
        }
    }

}
