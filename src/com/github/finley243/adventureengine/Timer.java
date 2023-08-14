package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.script.Script;

public class Timer extends GameInstanced {

    private final Script scriptExpire;
    private final Script scriptUpdate;
    private final Context context;
    private int roundsRemaining;

    public Timer(Game game, String ID, int rounds, Script scriptExpire, Script scriptUpdate, Context context) {
        super(game, ID);
        if (rounds <= 0) throw new IllegalArgumentException("Timer duration must be greater than 0");
        this.roundsRemaining = rounds;
        this.scriptExpire = scriptExpire;
        this.scriptUpdate = scriptUpdate;
        this.context = context;
    }

    public void update() {
        if (roundsRemaining > 0) {
            roundsRemaining -= 1;
            if (scriptUpdate != null && roundsRemaining > 0) {
                game().eventQueue().addToFront(new ScriptEvent(scriptUpdate, context));
            }
        }
        if (roundsRemaining <= 0 && scriptExpire != null) {
            game().eventQueue().addToFront(new ScriptEvent(scriptExpire, context));
        }
    }

    public boolean shouldRemove() {
        return roundsRemaining <= 0;
    }

}
