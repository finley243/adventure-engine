package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.stat.StatFloat;

public class ScriptDynamicStatFloat extends Script implements ScriptReturnTarget {

    private final StatFloat stat;
    private final float base;
    private final float min;
    private final float max;

    public ScriptDynamicStatFloat(StatFloat stat, float base, float min, float max) {
        this.stat = stat;
        this.base = base;
        this.min = min;
        this.max = max;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {

    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {

    }

}
