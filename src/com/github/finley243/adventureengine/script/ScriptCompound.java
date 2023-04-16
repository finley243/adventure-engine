package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.List;
import java.util.Map;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;
    // If true, only execute the first available script. If false, execute all scripts sequentially.
    private final boolean select;

    public ScriptCompound(Condition condition, Map<String, Variable> localParameters, List<Script> subScripts, boolean select) {
        super(condition, localParameters);
        this.subScripts = subScripts;
        this.select = select;
    }

    @Override
    public void executeSuccess(ContextScript context) {
        for (Script current : subScripts) {
            if (select) {
                boolean wasExecuted = current.execute(context);
                if(wasExecuted) {
                    break;
                }
            } else {
                current.execute(context);
            }
        }
    }

}
