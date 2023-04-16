package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.Map;

public class ScriptSetGlobal extends Script {

    private final String globalID;
    private final Variable variable;

    public ScriptSetGlobal(Condition condition, Map<String, Variable> localParameters, String globalID, Variable variable) {
        super(condition, localParameters);
        this.globalID = globalID;
        this.variable = variable;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        switch (variable.getDataType()) {
            case BOOLEAN -> context.game().data().setGlobalBoolean(globalID, variable.getValueBoolean(context));
            case INTEGER -> context.game().data().setGlobalInteger(globalID, variable.getValueInteger(context));
            case FLOAT -> context.game().data().setGlobalFloat(globalID, variable.getValueFloat(context));
            case STRING -> context.game().data().setGlobalString(globalID, variable.getValueString(context));
            case STRING_SET -> context.game().data().setGlobalStringSet(globalID, variable.getValueStringSet(context));
            default ->
                    throw new UnsupportedOperationException("No globals for provided data type: " + variable.getDataType());
        }
    }

}
