package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.script.Script;

public class Timer extends GameInstanced {

    private final Script expireScript;
    private int roundsRemaining;

    public Timer(Game game, String ID, int rounds, Script expireScript) {
        super(game, ID);
        if (rounds <= 0) throw new IllegalArgumentException("Timer duration must be greater than 0");
        this.roundsRemaining = rounds;
        this.expireScript = expireScript;
    }

    public void update() {
        if (roundsRemaining > 0) {
            roundsRemaining -= 1;
        }
        if (roundsRemaining <= 0 && expireScript != null) {
            expireScript.execute(new ContextScript(game(), game().data().getPlayer(), game().data().getPlayer(), null, null));
        }
    }

    public boolean shouldRemove() {
        return roundsRemaining <= 0;
    }

}
