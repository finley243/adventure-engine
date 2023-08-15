package com.github.finley243.adventureengine.event;

import java.util.Map;

public interface NumericMenuEvent {

    void onNumericMenuInput(Map<String, Integer> changedValues);

}
