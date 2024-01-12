package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.stat.StatInt;

public class ScriptDynamicStatInteger extends Script implements ScriptReturnTarget {

    private final StatInt stat;
    private final int base;
    private final int min;
    private final int max;

    public ScriptDynamicStatInteger(StatInt stat, int base, int min, int max) {
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
