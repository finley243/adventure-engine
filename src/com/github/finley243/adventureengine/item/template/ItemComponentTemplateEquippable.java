package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.action.ActionCustom;

import java.util.List;
import java.util.Set;

public class ItemComponentTemplateEquippable extends ItemComponentTemplate {

    private final Set<EquippableSlotsData> slots;

    public ItemComponentTemplateEquippable(boolean actionsRestricted, Set<EquippableSlotsData> slots) {
        super(actionsRestricted);
        this.slots = slots;
    }

    public Set<EquippableSlotsData> getSlots() {
        return slots;
    }

    public record EquippableSlotsData(Set<String> slots, Set<String> componentsExposed, List<String> equippedEffects, List<ActionCustom.CustomActionHolder> equippedActions) {}

}
