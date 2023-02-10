package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.List;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;
    // If true, only execute the first available script. If false, execute all scripts sequentially.
    private final boolean select;

    public ScriptCompound(Condition condition, List<Script> subScripts, boolean select) {
        super(condition);
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
