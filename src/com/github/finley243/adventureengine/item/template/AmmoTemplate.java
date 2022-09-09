package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.effect.AreaEffect;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmmoTemplate extends ItemTemplate {

    private final List<Effect> weaponEffects;
    private final boolean isReusable;
    private final boolean isThrowable;
    private final boolean isAreaTargeted;
    private final int damage;
    private final Damage.DamageType damageType;
    private final float armorMult;
    private final List<Effect> targetEffects;
    private final AreaEffect areaEffect;

    public AmmoTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, List<Effect> weaponEffects, boolean isReusable, boolean isThrowable, boolean isAreaTargeted, int damage, Damage.DamageType damageType, float armorMult, List<Effect> targetEffects, AreaEffect areaEffect) {
        super(ID, name, description, scripts, price);
        this.weaponEffects = weaponEffects;
        this.isReusable = isReusable;
        this.isThrowable = isThrowable;
        this.isAreaTargeted = isAreaTargeted;
        this.damage = damage;
        this.damageType = damageType;
        this.armorMult = armorMult;
        this.targetEffects = targetEffects;
        this.areaEffect = areaEffect;
    }

    @Override
    public boolean hasState() {
        return false;
    }

    public List<Effect> getWeaponEffects() {
        return weaponEffects;
    }

    public boolean isReusable() {
        return isReusable;
    }

    public boolean isThrowable() {
        return isThrowable;
    }

    public boolean isAreaTargeted() {
        return isAreaTargeted;
    }

    public int getDamage() {
        return damage;
    }

    public Damage.DamageType getDamageType() {
        return damageType;
    }

    public float getArmorMult() {
        return armorMult;
    }

    public List<Effect> getTargetEffects() {
        return targetEffects;
    }

    public AreaEffect getAreaEffect() {
        return areaEffect;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("ammo");
        return tags;
    }

}
