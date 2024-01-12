package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.stat.StatString;

public class ScriptDynamicStatString extends Script implements ScriptReturnTarget {

    private final StatString stat;
    private final String base;

    public ScriptDynamicStatString(StatString stat, String base) {
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
