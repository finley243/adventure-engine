package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
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
    private final StatInt clipSize;
    private final StatInt reloadActionPoints;
    private final StatFloat hitChanceModifier;
    private final StatFloat armorMult;
    private final StatString damageType;
    private final StatBoolean isSilenced;
    private final StatStringSet targetEffects;

    private Item ammoType;
    private int ammoCount;

    public ItemComponentWeapon(Item item, ItemComponentTemplateWeapon template) {
        super(item, template);
        this.attackTypes = new StatStringSet("attack_types", item);
        this.damage = new StatInt("damage", item);
        this.rate = new StatInt("rate", item);
        this.critDamage = new StatInt("crit_damage", item);
        this.critChance = new StatFloat("crit_chance", item);
        this.ranges = new StatStringSet("ranges", item);
        this.clipSize = new StatInt("clip_size", item);
        this.reloadActionPoints = new StatInt("reload_action_points", item);
        this.hitChanceModifier = new StatFloat("hit_chance_bonus", item);
        this.armorMult = new StatFloat("armor_mult", item);
        this.damageType = new StatString("damage_type", item);
        this.isSilenced = new StatBoolean("is_silenced", item, false);
        this.targetEffects = new StatStringSet("target_effects", item);
        this.ammoType = null;
        this.ammoCount = 0;
    }

    private ItemComponentTemplateWeapon getWeaponTemplate() {
        return (ItemComponentTemplateWeapon) getTemplate();
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        for (String attackType : getAttackTypes()) {
            actions.addAll(getItem().game().data().getAttackType(attackType).generateActions(subject, getItem()));
        }
        if (usesAmmo()) {
            for (String current : getAmmoTypes()) {
                actions.add(new ActionWeaponReload(getItem(), ItemFactory.create(getItem().game(), current)));
            }
        }
        return actions;
    }

    public WeaponClass getWeaponClass() {
        return getItem().game().data().getWeaponClass(getWeaponTemplate().getWeaponClass());
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

    public Set<AreaLink.DistanceCategory> getRanges(Context context) {
        return ranges.valueEnum(getWeaponClass().getPrimaryRanges(), AreaLink.DistanceCategory.class, context);
    }

    public float getModifiedHitChance(Context context, float baseChance) {
        return hitChanceModifier.value(baseChance, 0.0f, 1.0f, context);
    }

    public float getArmorMult(Context context) {
        return armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f, context);
    }

    public Set<String> getTargetEffects(Context context) {
        return targetEffects.value(getWeaponTemplate().getTargetEffects(), context);
    }

    public int getClipSize() {
        return clipSize.value(getWeaponTemplate().getClipSize(), 1, 100, new Context(getItem().game(), getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor(), getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor(), getItem()));
    }

    public String getDamageType(Context context) {
        return damageType.value(getWeaponTemplate().getDamageType(), context);
    }

    public int getAmmoRemaining() {
        return ammoCount;
    }

    public float getAmmoFraction() {
        if (getWeaponTemplate().getClipSize() == 0) return 1.0f;
        return ((float) ammoCount) / ((float) getWeaponTemplate().getClipSize());
    }

    public int reloadCapacity() {
        return getClipSize() - getAmmoRemaining();
    }

    public void setLoadedAmmoType(Item type) {
        if (ammoType != null) {
            ammoType.getComponentOfType(ItemComponentAmmo.class).onUnload(getItem());
        }
        this.ammoType = type;
        if (type != null) {
            type.getComponentOfType(ItemComponentAmmo.class).onLoad(getItem());
        }
    }

    public Item getLoadedAmmoType() {
        return ammoType;
    }

    public void loadAmmo(int amount) {
        ammoCount += amount;
    }

    public void consumeAmmo(int amount) {
        ammoCount -= amount;
        if (ammoCount <= 0) {
            ammoCount = 0;
            setLoadedAmmoType(null);
        }
    }

    public void emptyAmmo() {
        ammoCount = 0;
        setLoadedAmmoType(null);
    }

    public int getReloadActionPoints(Context context) {
        return reloadActionPoints.value(getWeaponTemplate().getReloadActionPoints(), 0, 1000, context);
    }

    public boolean isSilenced(Context context) {
        return isSilenced.value(getWeaponTemplate().isSilenced(), context);
    }

    public boolean isLoud(Context context) {
        return getWeaponClass().isLoud() && !isSilenced(context);
    }

    public boolean usesAmmo() {
        return getWeaponClass().usesAmmo();
    }

    public Set<String> getAmmoTypes() {
        return getWeaponClass().getAmmoTypes();
    }

    public String getSkill() {
        return getWeaponClass().getSkill();
    }

    public Set<String> getAttackTypes() {
        return attackTypes.value(getWeaponClass().getAttackTypes(), new Context(getItem().game(), getItem()));
    }

    @Override
    public StatInt getStatInt(String name) {
        return switch (name) {
            case "damage" -> damage;
            case "rate" -> rate;
            case "crit_damage" -> critDamage;
            case "clip_size" -> clipSize;
            case "reload_action_points" -> reloadActionPoints;
            default -> super.getStatInt(name);
        };
    }

    @Override
    public StatFloat getStatFloat(String name) {
        if ("hit_chance_modifier".equals(name)) {
            return hitChanceModifier;
        } else if ("armor_mult".equals(name)) {
            return armorMult;
        }
        return super.getStatFloat(name);
    }

    @Override
    public StatBoolean getStatBoolean(String name) {
        if ("is_silenced".equals(name)) {
            return isSilenced;
        }
        return super.getStatBoolean(name);
    }

    @Override
    public StatString getStatString(String name) {
        if ("damage_type".equals(name)) {
            return damageType;
        }
        return super.getStatString(name);
    }

    @Override
    public StatStringSet getStatStringSet(String name) {
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
            case "clip_size" -> Expression.constant(getClipSize());
            case "reload_action_points" -> Expression.constant(getReloadActionPoints(context));
            case "ammo_count" -> Expression.constant(ammoCount);
            case "armor_mult" -> Expression.constant(getArmorMult(context));
            case "is_silenced" -> Expression.constant(isSilenced(context));
            case "damage_type" -> Expression.constant(getDamageType(context));
            case "attack_types" -> Expression.constant(getAttackTypes());
            case "ranges" -> Expression.constant(ranges.valueFromEnum(getWeaponClass().getPrimaryRanges(), context));
            case "target_effects" -> Expression.constant(getTargetEffects(context));
            default -> super.getStatValue(name, context);
        };
    }

    @Override
    public void onStatChange(String name) {
        if ("clip_size".equals(name) && ammoCount > getClipSize()) {
            int difference = ammoCount - getClipSize();
            ammoCount = getClipSize();
            if (getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor() != null) {
                getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor().getInventory().addItems(ammoType.getTemplateID(), difference);
            }
        } else {
            super.onStatChange(name);
        }
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        if ("ammo_type".equals(name)) {
            return ammoType;
        }
        return super.getSubHolder(name, ID);
    }

}