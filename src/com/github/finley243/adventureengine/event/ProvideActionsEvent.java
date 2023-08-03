package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.List;

public record ProvideActionsEvent(List<Action> actions, Actor subject, Action lastAction, int actionRepeatCount) {
}
