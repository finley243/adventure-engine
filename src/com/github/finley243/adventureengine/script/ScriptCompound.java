package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.QueuedEvent;
import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;
    // If true, only execute the first available script. If false, execute all scripts sequentially.
    private final boolean select;

    public ScriptCompound(Condition condition, Map<String, Expression> localParameters, List<Script> subScripts, boolean select) {
        super(condition, localParameters);
        this.subScripts = subScripts;
        this.select = select;
    }

    @Override
    public void executeSuccess(Context context) {
        if (select) {
            for (Script current : subScripts) {
                if (current.canExecute(context)) {
                    context.game().eventQueue().addToFront(new ScriptEvent(current, context));
                    break;
                }
            }
        } else {
            List<QueuedEvent> scriptEvents = new ArrayList<>();
            for (Script current : subScripts) {
                scriptEvents.add(new ScriptEvent(current, context));
            }
            context.game().eventQueue().addAllToFront(scriptEvents);
        }
        context.game().eventQueue().executeNext();
    }

    @Override
    protected boolean generateInnerContext() {
        return true;
    }

}
