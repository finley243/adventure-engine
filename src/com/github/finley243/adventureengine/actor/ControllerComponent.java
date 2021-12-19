package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;

import java.util.List;

public interface ControllerComponent {

    Action chooseAction(List<Action> choices);

    void onStartTurn();

    void onStartAction();

    void onVisualEvent(VisualEvent e);

    void onSoundEvent(SoundEvent e);

}
