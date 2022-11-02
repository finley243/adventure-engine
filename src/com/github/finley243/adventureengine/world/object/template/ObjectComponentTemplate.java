package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;

public abstract class ObjectComponentTemplate extends GameInstanced {

    private final String ID;
    private final boolean startEnabled;

    public ObjectComponentTemplate(Game game, String ID, boolean startEnabled) {
        super(game);
        this.ID = ID;
        this.startEnabled = startEnabled;
    }

    public String getID() {
        return ID;
    }

    public boolean startEnabled() {
        return startEnabled;
    }

}
