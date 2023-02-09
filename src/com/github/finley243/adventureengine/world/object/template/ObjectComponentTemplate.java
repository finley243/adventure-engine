package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;

public abstract class ObjectComponentTemplate extends GameInstanced {

    private final boolean startEnabled;
    private final String name;

    public ObjectComponentTemplate(Game game, String ID, boolean startEnabled, String name) {
        super(game, ID);
        this.startEnabled = startEnabled;
        this.name = name;
    }

    public boolean startEnabled() {
        return startEnabled;
    }

    public String getName() {
        return name;
    }

}
