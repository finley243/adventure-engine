package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptExternal extends Script {

    private final String scriptID;

    public ScriptExternal(Condition condition, Map<String, Expression> localParameters, String scriptID) {
        super(condition, localParameters);
        this.scriptID = scriptID;
    }

    @Override
    protected void executeSuccess(Context context) {
        context.game().eventQueue().addToFront(new ScriptEvent(context.game().data().getScript(scriptID), context));
        context.game().eventQueue().executeNext();
    }

    @Override
    protected boolean generateInnerContext() {
        return true;
    }

}
