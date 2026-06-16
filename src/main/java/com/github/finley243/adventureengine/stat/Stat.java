package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public abstract class Stat {

    private final String name;
    private final StatHolder target;
    private final ScriptRuntime scriptRuntime;

    public Stat(String name, StatHolder target, ScriptRuntime scriptRuntime) {
        this.name = name;
        this.target = target;
        this.scriptRuntime = scriptRuntime;
    }

    public String getName() {
        return name;
    }

    public StatHolder getTarget() {
        return target;
    }

    protected boolean shouldApplyMod(Condition condition, Context context) {
        return condition == null || condition.isMet(scriptRuntime, context);
    }

}
