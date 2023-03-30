package com.github.finley243.adventureengine.effect;

import java.util.List;

public class AreaEffect {

    private final int duration;
    private final List<String> effects;

    public AreaEffect(int duration, List<String> effects) {
        this.duration = duration;
        this.effects = effects;
    }

    public int getDuration() {
        return duration;
    }

    public List<String> getEffects() {
        return effects;
    }

}
