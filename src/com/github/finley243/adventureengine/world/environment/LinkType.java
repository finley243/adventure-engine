package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;

public class LinkType extends GameInstanced {

    private final boolean isVisible;
    private final String moveAction;

    public LinkType(Game game, String ID, boolean isVisible, String moveAction) {
        super(game, ID);
        this.isVisible = isVisible;
        this.moveAction = moveAction;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getMoveAction() {
        return moveAction;
    }

}
