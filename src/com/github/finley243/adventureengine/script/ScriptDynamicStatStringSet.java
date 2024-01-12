package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.stat.StatStringSet;

import java.util.Set;

public class ScriptDynamicStatStringSet extends Script implements ScriptReturnTarget {

    private final StatStringSet stat;
    private final Set<String> base;

    public ScriptDynamicStatStringSet(StatStringSet stat, Set<String> base) {
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
