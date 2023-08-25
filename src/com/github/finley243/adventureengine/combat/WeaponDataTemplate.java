package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.*;

import java.util.Set;

public class WeaponDataTemplate implements StatHolder {

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

    public WeaponDataTemplate(int damage, int rate, int critDamage, float critChance, int clipSize, int reloadActionPoints, float armorMult, boolean silenced, String damageType, Set<String> targetEffects) {
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

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "damage" -> Expression.constant(damage);
            case "rate" -> Expression.constant(rate);
            case "crit_damage" -> Expression.constant(critDamage);
            case "crit_chance" -> Expression.constant(critChance);
            case "clip_size" -> Expression.constant(clipSize);
            case "reload_action_points" -> Expression.constant(reloadActionPoints);
            case "armor_mult" -> Expression.constant(armorMult);
            case "silenced" -> Expression.constant(silenced);
            case "damage_type" -> Expression.constant(damageType);
            case "target_effects" -> Expression.constant(targetEffects);
            case null, default -> null;
        };
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

}
