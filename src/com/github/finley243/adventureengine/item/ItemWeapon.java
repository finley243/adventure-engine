package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionModRemove;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.EffectComponent;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.item.template.WeaponTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.*;

public class ItemWeapon extends ItemEquippable implements MutableStatHolder {

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
	private final Map<String, List<ItemMod>> mods;
	private ItemAmmo ammoType;
	private int ammoCount;

	// TODO - EffectComponent round updates (handled by current inventory owner, either actor or area)
	private final EffectComponent effectComponent;
	
	public ItemWeapon(Game game, String ID, String templateID) {
		super(game, ID, templateID);
		this.attackTypes = new StatStringSet("attack_types", this);
		this.damage = new StatInt("damage", this);
		this.rate = new StatInt("rate", this);
		this.critDamage = new StatInt("crit_damage", this);
		this.critChance = new StatFloat("crit_chance", this);
		this.ranges = new StatStringSet("ranges", this);
		this.clipSize = new StatInt("clip_size", this);
		this.reloadActionPoints = new StatInt("reload_action_points", this);
		this.hitChanceModifier = new StatFloat("hit_chance_bonus", this);
		this.armorMult = new StatFloat("armor_mult", this);
		this.damageType = new StatString("damage_type", this);
		this.isSilenced = new StatBoolean("is_silenced", this, false);
		this.targetEffects = new StatStringSet("target_effects", this);
		this.mods = new HashMap<>();
		this.ammoType = null;
		this.ammoCount = 0;
		this.effectComponent = new EffectComponent(game, this, new Context(game, game.data().getPlayer(), game.data().getPlayer(), this));
	}

	private WeaponTemplate getWeaponTemplate() {
		return (WeaponTemplate) getTemplate();
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
		return clipSize.value(getWeaponTemplate().getClipSize(), 1, 100, new Context(game(), getEquippedActor(), getEquippedActor(), this));
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

	public void setLoadedAmmoType(ItemAmmo type) {
		if (ammoType != null) {
			ammoType.onUnload(this);
		}
		this.ammoType = type;
		if (type != null) {
			type.onLoad(this);
		}
	}

	public ItemAmmo getLoadedAmmoType() {
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
		return attackTypes.value(getWeaponClass().getAttackTypes(), new Context(game(), this));
	}

	public EffectComponent getEffectComponent() {
		return effectComponent;
	}

	public boolean canInstallMod(ItemMod mod) {
		String modSlot = mod.getModSlot();
		return getWeaponTemplate().getModSlots().containsKey(modSlot) && (!mods.containsKey(modSlot) || mods.get(modSlot).size() < getWeaponTemplate().getModSlots().get(modSlot));
	}

	public boolean hasModSlots() {
		return !getWeaponTemplate().getModSlots().isEmpty();
	}

	public void installMod(ItemMod mod) {
		for (String effectID : mod.getEffects()) {
			effectComponent.addEffect(effectID);
		}
		String modSlot = mod.getModSlot();
		if (!mods.containsKey(modSlot)) {
			mods.put(modSlot, new ArrayList<>());
		}
		mods.get(modSlot).add(mod);
	}

	public void removeMod(ItemMod mod) {
		for (String effectID : mod.getEffects()) {
			effectComponent.removeEffect(effectID);
		}
		String modSlot = mod.getModSlot();
		mods.get(modSlot).remove(mod);
		if (mods.get(modSlot).isEmpty()) {
			mods.remove(modSlot);
		}
	}

	@Override
	public boolean hasState() {
		return hasModSlots() || usesAmmo();
	}

	@Override
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = super.equippedActions(subject);
		for (String attackType : getAttackTypes()) {
			actions.addAll(game().data().getAttackType(attackType).generateActions(subject, this));
		}
		if (usesAmmo()) {
			for (String current : getAmmoTypes()) {
				actions.add(new ActionWeaponReload(this, (ItemAmmo) ItemFactory.create(game(), current)));
			}
		}
		return actions;
	}

	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		for (List<ItemMod> modList : mods.values()) {
			for (ItemMod mod : modList) {
				actions.add(new ActionModRemove(this, mod));
			}
		}
		return actions;
	}

	@Override
	public StatInt getStatInt(String name) {
		return switch (name) {
			case "damage" -> damage;
			case "rate" -> rate;
			case "crit_damage" -> critDamage;
			case "clip_size" -> clipSize;
			case "reload_action_points" -> reloadActionPoints;
			default -> null;
		};
	}

	@Override
	public StatFloat getStatFloat(String name) {
		if ("hit_chance_modifier".equals(name)) {
			return hitChanceModifier;
		} else if ("armor_mult".equals(name)) {
			return armorMult;
		}
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		if ("is_silenced".equals(name)) {
			return isSilenced;
		}
		return null;
	}

	@Override
	public StatString getStatString(String name) {
		if ("damage_type".equals(name)) {
			return damageType;
		}
		return null;
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
		return null;
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "damage" -> new ExpressionConstantInteger(damage.value(getWeaponTemplate().getDamage(), 1, 1000, context));
			case "rate" -> new ExpressionConstantInteger(rate.value(getWeaponTemplate().getRate(), 1, 50, context));
			case "crit_damage" -> new ExpressionConstantInteger(critDamage.value(getWeaponTemplate().getCritDamage(), 0, 1000, context));
			case "clip_size" -> new ExpressionConstantInteger(clipSize.value(getWeaponTemplate().getClipSize(), 1, 100, context));
			case "reload_action_points" -> new ExpressionConstantInteger(reloadActionPoints.value(getWeaponTemplate().getReloadActionPoints(), 0, 1000, context));
			case "ammo_count" -> new ExpressionConstantInteger(ammoCount);
			case "armor_mult" -> new ExpressionConstantFloat(armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f, context));
			case "is_silenced" -> new ExpressionConstantBoolean(isSilenced.value(getWeaponTemplate().isSilenced(), context));
			case "damage_type" -> new ExpressionConstantString(damageType.value(getWeaponTemplate().getDamageType(), context));
			case "attack_types" -> new ExpressionConstantStringSet(attackTypes.value(getWeaponClass().getAttackTypes(), context));
			case "ranges" -> new ExpressionConstantStringSet(ranges.valueFromEnum(getWeaponClass().getPrimaryRanges(), context));
			case "target_effects" -> new ExpressionConstantStringSet(targetEffects.value(getWeaponTemplate().getTargetEffects(), context));
			default -> super.getStatValue(name, context);
		};
	}

	@Override
	public void onStatChange(String name) {
		if ("clip_size".equals(name) && ammoCount > getClipSize()) {
			int difference = ammoCount - getClipSize();
			ammoCount = getClipSize();
			if (getEquippedActor() != null) {
				getEquippedActor().getInventory().addItems(ammoType.getTemplate().getID(), difference);
			}
		}
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		if ("ammo_type".equals(name)) {
			return ammoType;
		}
		return super.getSubHolder(name, ID);
	}

	@Override
	public void loadState(SaveData saveData) {
		if ("ammo_count".equals(saveData.getParameter())) {
			this.ammoCount = saveData.getValueInt();
		} else {
			super.loadState(saveData);
		}
	}

	@Override
	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if (ammoCount != getWeaponTemplate().getClipSize()) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "ammo_count", ammoCount));
		}
		return state;
	}

}
