package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionModRemove;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.EffectComponent;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
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
	
	private final String templateID;
	private final StatStringSet attackTypes;
	private final StatInt damage;
	private final StatInt rate;
	private final StatInt critDamage;
	private final StatFloat critChance;
	private final StatStringSet ranges;
	private final StatInt clipSize;
	private final StatFloat accuracyBonus;
	private final StatFloat armorMult;
	private final StatString damageType;
	private final StatBoolean isSilenced;
	private final StatStringSet targetEffects;
	private final Map<String, List<ItemWeaponMod>> mods;
	private ItemAmmo ammoType;
	private int ammoCount;

	// TODO - EffectComponent round updates (handled by current inventory owner, either actor or area)
	private final EffectComponent effectComponent;
	
	public ItemWeapon(Game game, String ID, String templateID) {
		super(game, ID);
		this.templateID = templateID;
		this.attackTypes = new StatStringSet("attackTypes", this);
		this.damage = new StatInt("damage", this);
		this.rate = new StatInt("rate", this);
		this.critDamage = new StatInt("critDamage", this);
		this.critChance = new StatFloat("critChance", this);
		this.ranges = new StatStringSet("ranges", this);
		this.clipSize = new StatInt("clipSize", this);
		this.accuracyBonus = new StatFloat("accuracyBonus", this);
		this.armorMult = new StatFloat("armorMult", this);
		this.damageType = new StatString("damageType", this);
		this.isSilenced = new StatBoolean("isSilenced", this, false);
		this.targetEffects = new StatStringSet("targetEffects", this);
		this.mods = new HashMap<>();
		this.ammoType = null;
		this.ammoCount = 0;
		this.effectComponent = new EffectComponent(game, this, new Context(game, game.data().getPlayer(), game.data().getPlayer(), this));
	}

	@Override
	public ItemTemplate getTemplate() {
		return getWeaponTemplate();
	}

	public WeaponTemplate getWeaponTemplate() {
		return (WeaponTemplate) game().data().getItemTemplate(templateID);
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

	// TODO - Change to accuracy multiplier?
	public float getAccuracyBonus(Context context) {
		return accuracyBonus.value(getWeaponTemplate().getAccuracyBonus(), -1.0f, 1.0f, context);
	}

	public float getArmorMult(Context context) {
		return armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f, context);
	}

	public Set<String> getTargetEffects(Context context) {
		return targetEffects.value(getWeaponTemplate().getTargetEffects(), context);
	}

	public int getClipSize() {
		return clipSize.value(getWeaponTemplate().getClipSize(), 0, 100, new Context(game(), this));
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

	public boolean isSilenced(Context context) {
		return isSilenced.value(getWeaponTemplate().isSilenced(), context);
	}

	public boolean isLoud(Context context) {
		return getWeaponClass().isLoud() && !isSilenced(context);
	}

	public Set<String> getAmmoTypes() {
		return getWeaponClass().getAmmoTypes();
	}

	public Actor.Skill getSkill() {
		return getWeaponClass().getSkill();
	}

	public Set<String> getAttackTypes() {
		return attackTypes.value(getWeaponClass().getAttackTypes(), new Context(game(), this));
	}

	public EffectComponent getEffectComponent() {
		return effectComponent;
	}

	public boolean canInstallMod(ItemWeaponMod mod) {
		String modSlot = mod.getWeaponModTemplate().getModSlot();
		return getWeaponTemplate().getModSlots().containsKey(modSlot) && (!mods.containsKey(modSlot) || mods.get(modSlot).size() < getWeaponTemplate().getModSlots().get(modSlot));
	}

	public void installMod(ItemWeaponMod mod) {
		for (String effectID : mod.getWeaponModTemplate().getWeaponEffects()) {
			effectComponent.addEffect(effectID);
		}
		String modSlot = mod.getWeaponModTemplate().getModSlot();
		if (!mods.containsKey(modSlot)) {
			mods.put(modSlot, new ArrayList<>());
		}
		mods.get(modSlot).add(mod);
	}

	public void removeMod(ItemWeaponMod mod) {
		for (String effectID : mod.getWeaponModTemplate().getWeaponEffects()) {
			effectComponent.removeEffect(effectID);
		}
		String modSlot = mod.getWeaponModTemplate().getModSlot();
		mods.get(modSlot).remove(mod);
		if (mods.get(modSlot).isEmpty()) {
			mods.remove(modSlot);
		}
	}

	@Override
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = super.equippedActions(subject);
		for (String attackType : getAttackTypes()) {
			actions.addAll(game().data().getAttackType(attackType).generateActions(subject, this));
		}
		if (getClipSize() > 0) {
			for (String current : getAmmoTypes()) {
				actions.add(new ActionWeaponReload(this, (ItemAmmo) ItemFactory.create(game(), current)));
			}
		}
		return actions;
	}

	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		for (List<ItemWeaponMod> modList : mods.values()) {
			for (ItemWeaponMod mod : modList) {
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
			default -> null;
		};
	}

	@Override
	public StatFloat getStatFloat(String name) {
		if ("accuracy_bonus".equals(name)) {
			return accuracyBonus;
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
	public int getValueInt(String name, Context context) {
		return switch (name) {
			case "damage" -> damage.value(getWeaponTemplate().getDamage(), 1, 1000, context);
			case "rate" -> rate.value(getWeaponTemplate().getRate(), 1, 50, context);
			case "crit_damage" -> critDamage.value(getWeaponTemplate().getCritDamage(), 0, 1000, context);
			case "clip_size" -> clipSize.value(getWeaponTemplate().getClipSize(), 0, 100, context);
			case "ammo_count" -> ammoCount;
			default -> super.getValueInt(name, context);
		};
	}

	@Override
	public float getValueFloat(String name, Context context) {
		return switch (name) {
			case "accuracy_bonus" -> accuracyBonus.value(getWeaponTemplate().getAccuracyBonus(), -1.0f, 1.0f, context);
			case "armor_mult" -> armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f, context);
			default -> super.getValueFloat(name, context);
		};
	}

	@Override
	public boolean getValueBoolean(String name, Context context) {
		if ("is_silenced".equals(name)) {
			return isSilenced.value(getWeaponTemplate().isSilenced(), context);
		}
		return super.getValueBoolean(name, context);
	}

	@Override
	public String getValueString(String name, Context context) {
		if ("damage_type".equals(name)) {
			return damageType.value(getWeaponTemplate().getDamageType(), context);
		}
		return super.getValueString(name, context);
	}

	@Override
	public Set<String> getValueStringSet(String name, Context context) {
		return switch (name) {
			case "attack_types" -> attackTypes.value(getWeaponClass().getAttackTypes(), context);
			case "ranges" -> ranges.valueFromEnum(getWeaponClass().getPrimaryRanges(), context);
			case "target_effects" -> targetEffects.value(getWeaponTemplate().getTargetEffects(), context);
			default -> super.getValueStringSet(name, context);
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
	public void modStateInteger(String name, int amount) {
		if ("ammo".equals(name)) {
			ammoCount = MathUtils.bound(ammoCount + amount, 0, getClipSize());
		} else {
			super.modStateInteger(name, amount);
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
