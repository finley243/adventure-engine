package com.github.finley243.adventureengine.script;

public interface ScriptReturnTarget {

    void onScriptReturn(RuntimeStack runtimeStack, Script.ScriptReturn scriptReturn);

}
