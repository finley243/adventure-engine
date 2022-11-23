package com.github.finley243.adventureengine;

public class GameInstanced {

    private final Game game;
    private final String ID;

    public GameInstanced(Game game, String ID) {
        this.game = game;
        this.ID = ID;
    }

    public Game game() {
        return game;
    }

    public String getID() {
        return ID;
    }

}
