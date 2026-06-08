package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;

public interface Effectible {

    void addEffect(Game game, Effect effect);

    void removeEffect(Game game, Effect effect);

}
