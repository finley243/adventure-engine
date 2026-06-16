package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public class Timer extends GameInstanced {

    private final Script scriptExpire;
    private final Script scriptUpdate;
    private final Context context;
    private int roundsRemaining;

    public Timer(String ID, int rounds, Script scriptExpire, Script scriptUpdate, Context context) {
        super(ID);
        if (rounds <= 0) throw new IllegalArgumentException("Timer duration must be greater than 0");
        this.roundsRemaining = rounds;
        this.scriptExpire = scriptExpire;
        this.scriptUpdate = scriptUpdate;
        this.context = context;
    }

    public void update(ScriptRuntime scriptRuntime) {
        if (roundsRemaining > 0) {
            roundsRemaining -= 1;
            if (scriptUpdate != null && roundsRemaining > 0) {
                scriptUpdate.run(scriptRuntime, context);
            }
        }
        if (roundsRemaining <= 0 && scriptExpire != null) {
            scriptExpire.run(scriptRuntime, context);
        }
    }

    public boolean shouldRemove() {
        return roundsRemaining <= 0;
    }

}
