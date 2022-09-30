package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.ArrayList;
import java.util.List;

public class StatEffectList {

    private final StatHolder target;
    private final List<Effect> additional;

    public StatEffectList(StatHolder target) {
        this.target = target;
        this.additional = new ArrayList<>();
    }

    public List<Effect> value(List<Effect> base) {
        List<Effect> outputList = new ArrayList<>(base);
        outputList.addAll(additional);
        return outputList;
    }

    public void addAdditional(List<Effect> values) {
        additional.addAll(values);
        target.onStatChange();
    }

    public void removeAdditional(List<Effect> values) {
        additional.removeAll(values);
        target.onStatChange();
    }

}
