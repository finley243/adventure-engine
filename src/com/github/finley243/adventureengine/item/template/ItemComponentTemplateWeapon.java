package com.github.finley243.adventureengine.item.template;

import java.util.Set;

public class ItemComponentTemplateWeapon extends ItemComponentTemplate {

    private final String weaponClass;
    private final int damage;
    private final int rate;
    private final int critDamage;
    private final float critChance;
    private final int clipSize;
    private final int reloadActionPoints;
    private final float armorMult;
    private final boolean silenced;
    private final String damageType;
    private final Set<String> targetEffects;

    public ItemComponentTemplateWeapon(boolean actionsRestricted, String weaponClass, int damage, int rate, int critDamage, float critChance, int clipSize, int reloadActionPoints, float armorMult, boolean silenced, String damageType, Set<String> targetEffects) {
        super(actionsRestricted);
        if (weaponClass == null) throw new IllegalArgumentException("Weapon class cannot be null");
        this.weaponClass = weaponClass;
        this.damage = damage;
        this.rate = rate;
        this.critDamage = critDamage;
        this.critChance = critChance;
        this.clipSize = clipSize;
        this.reloadActionPoints = reloadActionPoints;
        this.armorMult = armorMult;
        this.silenced = silenced;
        this.damageType = damageType;
        this.targetEffects = targetEffects;
    }

    public String getWeaponClass() {
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

    public int getClipSize() {
        return clipSize;
    }

    public int getReloadActionPoints() {
        return reloadActionPoints;
    }

    public float getArmorMult() {
        return armorMult;
    }

    public boolean isSilenced() {
        return silenced;
    }

    public String getDamageType() {
        return damageType;
    }

    public Set<String> getTargetEffects() {
        return targetEffects;
    }

}
