package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.*;

public class ScriptCompound extends Script implements ScriptReturnTarget {

    private final List<Script> subScripts;

    // TODO - Fix for recursive functions (values will be overwritten)
    private final Deque<Script> scriptQueue;
    private Context innerContext;

    public ScriptCompound(Condition condition, List<Script> subScripts, boolean select) {
        super(condition);
        this.subScripts = subScripts;
        this.scriptQueue = new ArrayDeque<>();
    }

    @Override
    public void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        scriptQueue.addAll(subScripts);
        this.innerContext = new Context(context);
        executeNextScript();
    }

    private void executeNextScript() {
        Script currentScript = scriptQueue.removeFirst();
        currentScript.execute(innerContext, this);
    }

    @Override
    public void onScriptReturn(ScriptReturn scriptReturn) {
        if (scriptReturn.error() != null) {
            sendReturn(scriptReturn);
        } else if (scriptReturn.isReturn()) {
            sendReturn(scriptReturn);
        } else if (scriptQueue.isEmpty()) {
            sendReturn(new ScriptReturn(null, false, false, null));
        } else {
            executeNextScript();
        }
    }

}
