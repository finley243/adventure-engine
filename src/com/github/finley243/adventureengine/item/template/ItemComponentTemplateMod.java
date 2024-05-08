package com.github.finley243.adventureengine.item.template;

import java.util.List;

public class ItemComponentTemplateMod extends ItemComponentTemplate {

    private final String modSlot;
    private final List<String> effects;

    public ItemComponentTemplateMod(boolean actionsRestricted, String modSlot, List<String> effects) {
        super(actionsRestricted);
        this.modSlot = modSlot;
        this.effects = effects;
    }

    public String getModSlot() {
        return modSlot;
    }

    public List<String> getEffects() {
        return effects;
    }

}
