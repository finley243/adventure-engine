package com.github.finley243.adventureengine;

public class GameInstanced {

    private final Game game;

    public GameInstanced(Game game) {
        this.game = game;
    }

    public Game game() {
        return game;
    }

}
