package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.ScriptValueHolder;

import java.util.Set;

public class WeaponDataTemplate implements ScriptValueHolder {

    private final int damage;
    private final int rate;
    private final int critDamage;
    private final float critChance;
    private final float armorMult;
    private final boolean silenced;
    private final String damageType;
    private final Set<String> targetEffects;

    public WeaponDataTemplate(int damage, int rate, int critDamage, float critChance, float armorMult, boolean silenced, String damageType, Set<String> targetEffects) {
        this.damage = damage;
        this.rate = rate;
        this.critDamage = critDamage;
        this.critChance = critChance;
        this.armorMult = armorMult;
        this.silenced = silenced;
        this.damageType = damageType;
        this.targetEffects = targetEffects;
    }

    @Override
    public Expression getScriptValue(String name, Context context) {
        return switch (name) {
            case "damage" -> Expression.constant(damage);
            case "rate" -> Expression.constant(rate);
            case "crit_damage" -> Expression.constant(critDamage);
            case "crit_chance" -> Expression.constant(critChance);
            case "armor_mult" -> Expression.constant(armorMult);
            case "silenced" -> Expression.constant(silenced);
            case "damage_type" -> Expression.constant(damageType);
            case "target_effects" -> Expression.constant(targetEffects);
            case null, default -> null;
        };
    }

    @Override
    public boolean setScriptValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public ScriptValueHolder getSubHolder(String name, String ID) {
        return null;
    }

}
