package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;

public class ContextScript {

    private final Game game;
    private final Actor subject;
    private final Actor target;

    public ContextScript(Game game, Actor subject, Actor target) {
        this.game = game;
        this.subject = subject;
        this.target = target;
    }

    public Game game() {
        return game;
    }

    public Actor getSubject() {
        return subject;
    }

    public Actor getTarget() {
        return target;
    }

}
