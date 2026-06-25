package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class ModItemComponentTemplate extends ItemComponentTemplate {

    private final String modSlot;
    private final List<Effect> effects;

    public ModItemComponentTemplate(boolean actionsRestricted, String modSlot, List<Effect> effects) {
        super(actionsRestricted);
        this.modSlot = modSlot;
        this.effects = effects;
    }

    public String getModSlot() {
        return modSlot;
    }

    public List<Effect> getEffects() {
        return effects;
    }

}
