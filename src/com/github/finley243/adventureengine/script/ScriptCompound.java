package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.List;

public class ScriptCompound extends Script {

    private final List<Script> subScripts;

    public ScriptCompound(Condition condition, List<Script> subScripts) {
        super(condition);
        this.subScripts = subScripts;
    }

    @Override
    public void execute(Actor subject) {
        if(canExecute(subject)) {
            for(Script current : subScripts) {
                current.execute(subject);
            }
        }
    }

}
