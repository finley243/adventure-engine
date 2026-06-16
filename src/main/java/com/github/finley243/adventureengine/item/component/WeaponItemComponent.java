package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.WeaponItemComponentTemplate;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class WeaponItemComponent extends ItemComponent {

    public static final float HIT_CHANCE_BASE_MELEE_MIN = 0.10f;
    public static final float HIT_CHANCE_BASE_MELEE_MAX = 0.90f;
    public static final float HIT_CHANCE_BASE_RANGED_MIN = 0.10f;
    public static final float HIT_CHANCE_BASE_RANGED_MAX = 0.90f;

    private final StringSetRegistryStat<WeaponAttackType> attackTypes;
    private final IntStat damage;
    private final IntStat rate;
    private final IntStat critDamage;
    private final FloatStat critChance;
    private final StringSetStat ranges;
    private final FloatStat hitChanceModifier;
    private final FloatStat armorMult;
    private final StringStat damageType;
    private final BooleanStat isSilenced;
    private final StringSetRegistryStat<Effect> targetEffects;

    public WeaponItemComponent(Item item, WeaponItemComponentTemplate template, Registry<WeaponAttackType> attackTypeRegistry, Registry<Effect> effectRegistry) {
        super(item, template);
        this.attackTypes = new StringSetRegistryStat<>("attack_types", item, attackTypeRegistry, WeaponAttackType::getID);
        this.damage = new IntStat("damage", item);
        this.rate = new IntStat("rate", item);
        this.critDamage = new IntStat("crit_damage", item);
        this.critChance = new FloatStat("crit_chance", item);
        this.ranges = new StringSetStat("ranges", item);
        this.hitChanceModifier = new FloatStat("hit_chance_bonus", item);
        this.armorMult = new FloatStat("armor_mult", item);
        this.damageType = new StringStat("damage_type", item);
        this.isSilenced = new BooleanStat("is_silenced", item, false);
        this.targetEffects = new StringSetRegistryStat<>("target_effects", item, effectRegistry, Effect::getID);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private WeaponItemComponentTemplate getWeaponTemplate() {
        return (WeaponItemComponentTemplate) getTemplate();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(ScriptRuntime scriptRuntime, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(scriptRuntime, subject);
        for (WeaponAttackType attackType : getAttackTypes(scriptRuntime, Context.builder().subject(subject).parentItem(getItem()).build())) {
            actions.addAll(attackType.generateActions(subject, getItem()));
        }
        return actions;
    }

    public WeaponClass getWeaponClass() {
        return getWeaponTemplate().getWeaponClass();
    }

    public boolean isRanged() {
        return getWeaponClass().isRanged();
    }

    public int getDamage(Context context) {
        return damage.value(getWeaponTemplate().getDamage(), 1, 1000, context);
    }

    public int getRate(Context context) {
        return rate.value(getWeaponTemplate().getRate(), 1, 50, context);
    }

    public float getBaseHitChanceMin() {
        return isRanged() ? HIT_CHANCE_BASE_RANGED_MIN : HIT_CHANCE_BASE_MELEE_MIN;
    }

    public float getBaseHitChanceMax() {
        return isRanged() ? HIT_CHANCE_BASE_RANGED_MAX : HIT_CHANCE_BASE_MELEE_MAX;
    }

    public int getCritDamage(Context context) {
        return critDamage.value(getWeaponTemplate().getCritDamage(), 0, 1000, context);
    }

    public float getCritChance(Context context) {
        return critChance.value(getWeaponTemplate().getCritChance(), 0.0f, 1.0f, context);
    }

    public Set<AreaLink.DistanceCategory> getRanges(ScriptRuntime scriptRuntime, Context context) {
        return ranges.valueEnum(getWeaponClass().primaryRanges(), AreaLink.DistanceCategory.class, scriptRuntime, context);
    }

    public float getModifiedHitChance(Context context, float baseChance) {
        return hitChanceModifier.value(baseChance, 0.0f, 1.0f, context);
    }

    public float getArmorMult(Context context) {
        return armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f, context);
    }

    public Set<String> getTargetEffects(ScriptRuntime scriptRuntime, Context context) {
        return targetEffects.value(getWeaponTemplate().getTargetEffects(), scriptRuntime, context);
    }

    public String getDamageType(Context context) {
        return damageType.value(getWeaponTemplate().getDamageType(), context);
    }

    public boolean isSilenced(Context context) {
        return isSilenced.value(getWeaponTemplate().isSilenced(), context);
    }

    public boolean isLoud(Context context) {
        return getWeaponClass().isLoud() && !isSilenced(context);
    }

    public String getSkill() {
        return getWeaponClass().skill();
    }

    public Set<WeaponAttackType> getAttackTypes(ScriptRuntime scriptRuntime, Context context) {
        return attackTypes.valueObjects(getWeaponClass().attackTypes(), scriptRuntime, context);
    }

    @Override
    public IntStat getStatInt(String name) {
        return switch (name) {
            case "damage" -> damage;
            case "rate" -> rate;
            case "crit_damage" -> critDamage;
            default -> super.getStatInt(name);
        };
    }

    @Override
    public FloatStat getStatFloat(String name) {
        if ("hit_chance_modifier".equals(name)) {
            return hitChanceModifier;
        } else if ("armor_mult".equals(name)) {
            return armorMult;
        }
        return super.getStatFloat(name);
    }

    @Override
    public BooleanStat getStatBoolean(String name) {
        if ("is_silenced".equals(name)) {
            return isSilenced;
        }
        return super.getStatBoolean(name);
    }

    @Override
    public StringStat getStatString(String name) {
        if ("damage_type".equals(name)) {
            return damageType;
        }
        return super.getStatString(name);
    }

    @Override
    public StringSetStat getStatStringSet(String name) {
        if ("ranges".equals(name)) {
            return ranges;
        } else if ("attack_types".equals(name)) {
            return attackTypes;
        } else if ("target_effects".equals(name)) {
            return targetEffects;
        }
        return super.getStatStringSet(name);
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "damage" -> Expression.constant(getDamage(context));
            case "rate" -> Expression.constant(getRate(context));
            case "crit_damage" -> Expression.constant(getCritDamage(context));
            case "armor_mult" -> Expression.constant(getArmorMult(context));
            case "is_silenced" -> Expression.constant(isSilenced(context));
            case "damage_type" -> Expression.constant(getDamageType(context));
            case "attack_types" -> Expression.constant(getAttackTypes(context));
            case "ranges" -> Expression.constant(ranges.valueFromEnum(getWeaponClass().primaryRanges(), context));
            case "target_effects" -> Expression.constant(getTargetEffects(context));
            case "skill" -> Expression.constant(getSkill());
            default -> super.getStatValue(name, context);
        };
    }

}
