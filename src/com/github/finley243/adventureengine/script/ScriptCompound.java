package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.QueuedEvent;
import com.github.finley243.adventureengine.event.ScriptEvent;

import java.util.ArrayList;
import java.util.List;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;
    // If true, only execute the first available script. If false, execute all scripts sequentially.
    private final boolean select;

    public ScriptCompound(Condition condition, List<Script> subScripts, boolean select) {
        super(condition);
        this.subScripts = subScripts;
        this.select = select;
    }

    @Override
    public void executeSuccess(Context context) {
        Context innerContext = new Context(context);
        if (select) {
            for (Script current : subScripts) {
                if (current.canExecute(innerContext)) {
                    context.game().eventQueue().addToFront(new ScriptEvent(current, innerContext));
                    break;
                }
            }
        } else {
            List<QueuedEvent> scriptEvents = new ArrayList<>();
            for (Script current : subScripts) {
                scriptEvents.add(new ScriptEvent(current, innerContext));
            }
            context.game().eventQueue().addAllToFront(scriptEvents);
        }
        context.game().eventQueue().executeNext();
    }

}
