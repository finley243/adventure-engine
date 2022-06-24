package com.github.finley243.adventureengine.effect.moddable;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.ArrayList;
import java.util.List;

public class ModdableEffectList {

    private final Moddable target;
    private final List<Effect> additional;

    public ModdableEffectList(Moddable target) {
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
