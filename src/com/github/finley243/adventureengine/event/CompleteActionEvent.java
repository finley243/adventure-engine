package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;

public record CompleteActionEvent(Action action, int repeatActionCount) {
}
