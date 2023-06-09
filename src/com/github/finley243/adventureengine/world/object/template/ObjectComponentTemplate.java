package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;

public abstract class ObjectComponentTemplate extends GameInstanced {

    private final boolean startEnabled;
    // If true, actions are not added to object actions
    private final boolean actionsRestricted;
    private final String name;

    public ObjectComponentTemplate(Game game, String ID, boolean startEnabled, boolean actionsRestricted, String name) {
        super(game, ID);
        this.startEnabled = startEnabled;
        this.actionsRestricted = actionsRestricted;
        this.name = name;
    }

    public boolean startEnabled() {
        return startEnabled;
    }

    public boolean actionsRestricted() {
        return actionsRestricted;
    }

    public String getName() {
        return name;
    }

}
