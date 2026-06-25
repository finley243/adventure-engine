package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;

import java.util.Set;

public class WeaponItemComponentTemplate extends ItemComponentTemplate {

    private final WeaponClass weaponClass;
    private final int damage;
    private final int rate;
    private final int critDamage;
    private final float critChance;
    private final float armorMult;
    private final boolean silenced;
    private final DamageType damageType;
    private final Set<Effect> targetEffects;

    public WeaponItemComponentTemplate(boolean actionsRestricted, WeaponClass weaponClass, int damage, int rate, int critDamage, float critChance, float armorMult, boolean silenced, DamageType damageType, Set<Effect> targetEffects) {
        super(actionsRestricted);
        if (weaponClass == null) throw new IllegalArgumentException("Weapon class cannot be null");
        this.weaponClass = weaponClass;
        this.damage = damage;
        this.rate = rate;
        this.critDamage = critDamage;
        this.critChance = critChance;
        this.armorMult = armorMult;
        this.silenced = silenced;
        this.damageType = damageType;
        this.targetEffects = targetEffects;
    }

    public WeaponClass getWeaponClass() {
        return weaponClass;
    }

    public int getDamage() {
        return damage;
    }

    public int getRate() {
        return rate;
    }

    public int getCritDamage() {
        return critDamage;
    }

    public float getCritChance() {
        return critChance;
    }

    public float getArmorMult() {
        return armorMult;
    }

    public boolean isSilenced() {
        return silenced;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public Set<Effect> getTargetEffects() {
        return targetEffects;
    }

}
