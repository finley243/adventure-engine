package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;

public interface QueuedEvent {

    void execute(Game game);

    boolean continueAfterExecution();

}
