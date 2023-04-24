package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.script.Script;

public class Timer extends GameInstanced {

    private final Script expireScript;
    private final Context context;
    private int roundsRemaining;

    public Timer(Game game, String ID, int rounds, Script expireScript, Context context) {
        super(game, ID);
        if (rounds <= 0) throw new IllegalArgumentException("Timer duration must be greater than 0");
        this.roundsRemaining = rounds;
        this.expireScript = expireScript;
        this.context = context;
    }

    public void update() {
        if (roundsRemaining > 0) {
            roundsRemaining -= 1;
        }
        if (roundsRemaining <= 0 && expireScript != null) {
            expireScript.execute(context);
        }
    }

    public boolean shouldRemove() {
        return roundsRemaining <= 0;
    }

}
