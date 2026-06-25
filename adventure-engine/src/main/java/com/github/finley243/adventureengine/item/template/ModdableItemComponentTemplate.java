package com.github.finley243.adventureengine.item.template;

import java.util.Map;

public class ModdableItemComponentTemplate extends ItemComponentTemplate {

    private final Map<String, Integer> modSlots;

    public ModdableItemComponentTemplate(boolean actionsRestricted, Map<String, Integer> modSlots) {
        super(actionsRestricted);
        this.modSlots = modSlots;
    }

    public Map<String, Integer> getModSlots() {
        return modSlots;
    }

}
