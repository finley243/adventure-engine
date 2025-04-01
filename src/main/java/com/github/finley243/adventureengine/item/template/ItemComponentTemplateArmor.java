package com.github.finley243.adventureengine.item.template;

import java.util.Map;
import java.util.Set;

public class ItemComponentTemplateArmor extends ItemComponentTemplate {

    // This value is subtracted from the original damage (key: damage type)
    private final Map<String, Integer> damageResistance;
    // This value is a percentage of the original damage which is subtracted from the original damage (key: damage type)
    private final Map<String, Float> damageMult;
    // Damage resistances/mults will be applied to damage that targets these limbs
    private final Set<String> coveredLimbs;
    // Whether damage resistances/mults will be applied to non-targeted damage
    private final boolean coversMainBody;

    public ItemComponentTemplateArmor(boolean actionsRestricted, Map<String, Integer> damageResistance, Map<String, Float> damageMult, Set<String> coveredLimbs, boolean coversMainBody) {
        super(actionsRestricted);
        this.damageResistance = damageResistance;
        this.damageMult = damageMult;
        this.coveredLimbs = coveredLimbs;
        this.coversMainBody = coversMainBody;
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
