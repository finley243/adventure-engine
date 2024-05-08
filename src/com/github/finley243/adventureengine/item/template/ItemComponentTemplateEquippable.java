package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.action.ActionCustom;

import java.util.List;
import java.util.Set;

public class ItemComponentTemplateEquippable extends ItemComponentTemplate {

    private final Set<Set<String>> slots;
    private final List<String> equippedEffects;
    private final List<ActionCustom.CustomActionHolder> equippedActions;

    public ItemComponentTemplateEquippable(boolean actionsRestricted, Set<Set<String>> slots, List<String> equippedEffects, List<ActionCustom.CustomActionHolder> equippedActions) {
        super(actionsRestricted);
        this.slots = slots;
        this.equippedEffects = equippedEffects;
        this.equippedActions = equippedActions;
    }

    public Set<Set<String>> getSlots() {
        return slots;
    }

    public List<String> getEquippedEffects() {
        return equippedEffects;
    }

    public List<ActionCustom.CustomActionHolder> getEquippedActions() {
        return equippedActions;
    }

    public record EquippableSlotsData(Set<String> slots, Set<String> componentsExposed) {}

}
