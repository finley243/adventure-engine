package com.github.finley243.adventureengine;

public abstract class GameInstanced {

    private final String ID;

    public GameInstanced(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameInstanced oInstanced)) {
            return false;
        } else if (!(oInstanced.getClass().equals(this.getClass()))) {
            return false;
        } else {
            return oInstanced.ID.equals(this.ID);
        }
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

}
