package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.action.ActionCustom;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemComponentTemplateEquippable extends ItemComponentTemplate {

    private final Set<Set<String>> slots;
    private final List<String> equippedEffects;
    private final List<ActionCustom.CustomActionHolder> equippedActions;
    // This value is subtracted from the original damage (key: damage type)
    private final Map<String, Integer> damageResistance;
    // This value is a percentage of the original damage which is subtracted from the original damage (key: damage type)
    private final Map<String, Float> damageMult;
    // Damage resistances/mults will be applied to damage that targets these limbs
    private final Set<String> coveredLimbs;
    // Whether damage resistances/mults will be applied to non-targeted damage
    private final boolean coversMainBody;

    public ItemComponentTemplateEquippable(Set<Set<String>> slots, List<String> equippedEffects, List<ActionCustom.CustomActionHolder> equippedActions, Map<String, Integer> damageResistance, Map<String, Float> damageMult, Set<String> coveredLimbs, boolean coversMainBody) {
        this.slots = slots;
        this.equippedEffects = equippedEffects;
        this.equippedActions = equippedActions;
        this.damageResistance = damageResistance;
        this.damageMult = damageMult;
        this.coveredLimbs = coveredLimbs;
        this.coversMainBody = coversMainBody;
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

    public int getDamageResistance(String damageType) {
        return damageResistance.getOrDefault(damageType, 0);
    }

    public float getDamageMult(String damageType) {
        return damageMult.getOrDefault(damageType, 0.0f);
    }

    public Set<String> getCoveredLimbs() {
        return coveredLimbs;
    }

    public boolean coversMainBody() {
        return coversMainBody;
    }

}
