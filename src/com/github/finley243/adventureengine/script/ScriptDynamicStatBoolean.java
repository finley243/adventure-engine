package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.stat.StatBoolean;

public class ScriptDynamicStatBoolean extends Script implements ScriptReturnTarget {

    private final StatBoolean stat;
    private final boolean base;

    public ScriptDynamicStatBoolean(StatBoolean stat, boolean base) {
        this.stat = stat;
        this.base = base;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {

    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {

    }

}
