package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.script.Script;

public class Timer extends GameInstanced {

    private final String ID;
    private final Script expireScript;
    private int roundsRemaining;

    public Timer(Game game, String ID, int rounds, Script expireScript) {
        super(game);
        if (rounds <= 0) throw new IllegalArgumentException("Timer duration must be greater than 0");
        this.ID = ID;
        this.roundsRemaining = rounds;
        this.expireScript = expireScript;
    }

    public String getID() {
        return ID;
    }

    public void update() {
        if (roundsRemaining > 0) {
            roundsRemaining -= 1;
        }
        if (roundsRemaining <= 0 && expireScript != null) {
            expireScript.execute(game().data().getPlayer(), game().data().getPlayer());
        }
    }

    public boolean shouldRemove() {
        return roundsRemaining <= 0;
    }

}
