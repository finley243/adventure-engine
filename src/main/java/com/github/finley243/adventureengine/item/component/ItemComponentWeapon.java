package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateWeapon;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ItemComponentWeapon extends ItemComponent {

    public static final float HIT_CHANCE_BASE_MELEE_MIN = 0.10f;
    public static final float HIT_CHANCE_BASE_MELEE_MAX = 0.90f;
    public static final float HIT_CHANCE_BASE_RANGED_MIN = 0.10f;
    public static final float HIT_CHANCE_BASE_RANGED_MAX = 0.90f;

    private final StatStringSet attackTypes;
    private final StatInt damage;
    private final StatInt rate;
    private final StatInt critDamage;
    private final StatFloat critChance;
    private final StatStringSet ranges;
    private final StatFloat hitChanceModifier;
    private final StatFloat armorMult;
    private final StatString damageType;
    private final StatBoolean isSilenced;
    private final StatStringSet targetEffects;

    public ItemComponentWeapon(Item item, ItemComponentTemplateWeapon template) {
        super(item, template);
        this.attackTypes = new StatStringSet("attack_types", item);
        this.damage = new StatInt("damage", item);
        this.rate = new StatInt("rate", item);
        this.critDamage = new StatInt("crit_damage", item);
        this.critChance = new StatFloat("crit_chance", item);
        this.ranges = new StatStringSet("ranges", item);
        this.hitChanceModifier = new StatFloat("hit_chance_bonus", item);
        this.armorMult = new StatFloat("armor_mult", item);
        this.damageType = new StatString("damage_type", item);
        this.isSilenced = new StatBoolean("is_silenced", item, false);
        this.targetEffects = new StatStringSet("target_effects", item);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private ItemComponentTemplateWeapon getWeaponTemplate() {
        return (ItemComponentTemplateWeapon) getTemplate();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(subject);
        for (String attackType : getAttackTypes(subject.game())) {
            actions.addAll(getItem().game().data().getAttackType(attackType).generateActions(subject.game(), subject, getItem()));
        }
        return actions;
    }

    public WeaponClass getWeaponClass() {
        return getItem().game().data().getWeaponClass(getWeaponTemplate().getWeaponClass());
    }

    public boolean isRanged() {
        return getWeaponClass().isRanged();
    }

    public int getDamage(Game game, Context context) {
        return damage.value(getWeaponTemplate().getDamage(), 1, 1000, game, context);
    }

    public int getRate(Game game, Context context) {
        return rate.value(getWeaponTemplate().getRate(), 1, 50, game, context);
    }

    public float getBaseHitChanceMin() {
        return isRanged() ? HIT_CHANCE_BASE_RANGED_MIN : HIT_CHANCE_BASE_MELEE_MIN;
    }

    public float getBaseHitChanceMax() {
        return isRanged() ? HIT_CHANCE_BASE_RANGED_MAX : HIT_CHANCE_BASE_MELEE_MAX;
    }

    public int getCritDamage(Game game, Context context) {
        return critDamage.value(getWeaponTemplate().getCritDamage(), 0, 1000, game, context);
    }

    public float getCritChance(Game game, Context context) {
        return critChance.value(getWeaponTemplate().getCritChance(), 0.0f, 1.0f, game, context);
    }

    public Set<AreaLink.DistanceCategory> getRanges(Game game, Context context) {
        return ranges.valueEnum(getWeaponClass().primaryRanges(), AreaLink.DistanceCategory.class, game, context);
    }

    public float getModifiedHitChance(Game game, Context context, float baseChance) {
        return hitChanceModifier.value(baseChance, 0.0f, 1.0f, game, context);
    }

    public float getArmorMult(Game game, Context context) {
        return armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f, game, context);
    }

    public Set<String> getTargetEffects(Game game, Context context) {
        return targetEffects.value(getWeaponTemplate().getTargetEffects(), game, context);
    }

    public String getDamageType(Game game, Context context) {
        return damageType.value(getWeaponTemplate().getDamageType(), game, context);
    }

    public boolean isSilenced(Game game, Context context) {
        return isSilenced.value(getWeaponTemplate().isSilenced(), game, context);
    }

    public boolean isLoud(Game game, Context context) {
        return getWeaponClass().isLoud() && !isSilenced(game, context);
    }

    public String getSkill() {
        return getWeaponClass().skill();
    }

    public Set<String> getAttackTypes(Game game) {
        return attackTypes.value(getWeaponClass().attackTypes(), game, new Context((Actor) null, null, getItem()));
    }

    @Override
    public StatInt getStatInt(String name, Game game) {
        return switch (name) {
            case "damage" -> damage;
            case "rate" -> rate;
            case "crit_damage" -> critDamage;
            default -> super.getStatInt(name, game);
        };
    }

    @Override
    public StatFloat getStatFloat(String name, Game game) {
        if ("hit_chance_modifier".equals(name)) {
            return hitChanceModifier;
        } else if ("armor_mult".equals(name)) {
            return armorMult;
        }
        return super.getStatFloat(name, game);
    }

    @Override
    public StatBoolean getStatBoolean(String name, Game game) {
        if ("is_silenced".equals(name)) {
            return isSilenced;
        }
        return super.getStatBoolean(name, game);
    }

    @Override
    public StatString getStatString(String name, Game game) {
        if ("damage_type".equals(name)) {
            return damageType;
        }
        return super.getStatString(name, game);
    }

    @Override
    public StatStringSet getStatStringSet(String name, Game game) {
        if ("ranges".equals(name)) {
            return ranges;
        } else if ("attack_types".equals(name)) {
            return attackTypes;
        } else if ("target_effects".equals(name)) {
            return targetEffects;
        }
        return super.getStatStringSet(name, game);
    }

    @Override
    public Expression getStatValue(String name, Game game, Context context) {
        return switch (name) {
            case "damage" -> Expression.constant(getDamage(game, context));
            case "rate" -> Expression.constant(getRate(game, context));
            case "crit_damage" -> Expression.constant(getCritDamage(game, context));
            case "armor_mult" -> Expression.constant(getArmorMult(game, context));
            case "is_silenced" -> Expression.constant(isSilenced(game, context));
            case "damage_type" -> Expression.constant(getDamageType(game, context));
            case "attack_types" -> Expression.constant(getAttackTypes(game));
            case "ranges" -> Expression.constant(ranges.valueFromEnum(getWeaponClass().primaryRanges(), game, context));
            case "target_effects" -> Expression.constant(getTargetEffects(game, context));
            case "skill" -> Expression.constant(getSkill());
            default -> super.getStatValue(name, game, context);
        };
    }

}
