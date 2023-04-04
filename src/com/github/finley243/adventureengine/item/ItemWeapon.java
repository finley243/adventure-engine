package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.WeaponTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.*;

public class ItemWeapon extends ItemEquippable implements EffectableStatHolder {

	public static final float HIT_CHANCE_BASE_MELEE_MIN = 0.10f;
	public static final float HIT_CHANCE_BASE_MELEE_MAX = 0.90f;
	public static final float HIT_CHANCE_BASE_RANGED_MIN = 0.10f;
	public static final float HIT_CHANCE_BASE_RANGED_MAX = 0.90f;
	
	private final String templateID;
	private final StatStringSet attackTypes;
	private final StatInt damage;
	private final StatInt rate;
	private final StatInt critDamage;
	private final StatStringSet ranges;
	private final StatInt clipSize;
	private final StatFloat accuracyBonus;
	private final StatFloat armorMult;
	private final StatString damageType;
	private final StatBoolean isSilenced;
	private ItemAmmo ammoType;
	private int ammoCount;

	private final Map<Effect, List<Integer>> effects;
	
	public ItemWeapon(Game game, String ID, String templateID) {
		super(game, ID);
		this.templateID = templateID;
		this.attackTypes = new StatStringSet(this);
		this.damage = new StatInt(this);
		this.rate = new StatInt(this);
		this.critDamage = new StatInt(this);
		this.ranges = new StatStringSet(this);
		this.clipSize = new StatInt(this);
		this.accuracyBonus = new StatFloat(this);
		this.armorMult = new StatFloat(this);
		this.damageType = new StatString(this);
		this.isSilenced = new StatBoolean(this, false);
		this.ammoType = null;
		this.ammoCount = 0;
		this.effects = new HashMap<>();
	}

	@Override
	public ItemTemplate getTemplate() {
		return getWeaponTemplate();
	}

	public WeaponTemplate getWeaponTemplate() {
		return (WeaponTemplate) game().data().getItem(templateID);
	}

	public WeaponClass getWeaponClass() {
		return game().data().getWeaponClass(getWeaponTemplate().getWeaponClass());
	}
	
	public boolean isRanged() {
		return getWeaponClass().isRanged();
	}
	
	public int getDamage() {
		return damage.value(getWeaponTemplate().getDamage(), 1, 1000);
	}
	
	public int getRate() {
		return rate.value(getWeaponTemplate().getRate(), 1, 50);
	}

	public float getBaseHitChanceMin() {
		return isRanged() ? HIT_CHANCE_BASE_RANGED_MIN : HIT_CHANCE_BASE_MELEE_MIN;
	}

	public float getBaseHitChanceMax() {
		return isRanged() ? HIT_CHANCE_BASE_RANGED_MAX : HIT_CHANCE_BASE_MELEE_MAX;
	}
	
	public int getCritDamage() {
		return critDamage.value(getWeaponTemplate().getCritDamage(), 0, 1000);
	}

	public Set<AreaLink.DistanceCategory> getRanges() {
		return ranges.valueEnum(getWeaponClass().getPrimaryRanges(), AreaLink.DistanceCategory.class);
	}

	// TODO - Change to accuracy multiplier?
	public float getAccuracyBonus() {
		return accuracyBonus.value(getWeaponTemplate().getAccuracyBonus(), -1.0f, 1.0f);
	}

	public float getArmorMult() {
		return armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f);
	}

	// TODO - Add target effects to weapons
	public List<String> getTargetEffects() {
		return new ArrayList<>();
	}

	public int getClipSize() {
		return clipSize.value(getWeaponTemplate().getClipSize(), 0, 100);
	}

	public Damage.DamageType getDamageType() {
		return damageType.valueEnum(getWeaponTemplate().getDamageType(), Damage.DamageType.class);
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

	// TODO - Use isSilenced value to determine isLoud parameter of attack events
	public boolean isSilenced() {
		return isSilenced.value(getWeaponTemplate().isSilenced());
	}

	public Set<String> getAmmoTypes() {
		return getWeaponClass().getAmmoTypes();
	}

	public Actor.Skill getSkill() {
		return getWeaponClass().getSkill();
	}

	public Set<String> getAttackTypes() {
		return attackTypes.value(getWeaponClass().getAttackTypes());
	}

	@Override
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = super.equippedActions(subject);
		for (String attackType : getAttackTypes()) {
			actions.addAll(game().data().getAttackType(attackType).generateActions(subject, this, this));
		}
		if (getClipSize() > 0) {
			for (String current : getWeaponClass().getAmmoTypes()) {
				actions.add(new ActionWeaponReload(this, (ItemAmmo) ItemFactory.create(game(), current)));
			}
		}
		return actions;
	}

	@Override
	public StatInt getStatInt(String name) {
		return switch (name) {
			case "damage" -> damage;
			case "rate" -> rate;
			case "critDamage" -> critDamage;
			case "clipSize" -> clipSize;
			default -> null;
		};
	}

	@Override
	public StatFloat getStatFloat(String name) {
		if ("accuracyBonus".equals(name)) {
			return accuracyBonus;
		} else if ("armorMult".equals(name)) {
			return armorMult;
		}
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		if ("isSilenced".equals(name)) {
			return isSilenced;
		}
		return null;
	}

	@Override
	public StatString getStatString(String name) {
		if ("damageType".equals(name)) {
			return damageType;
		}
		return null;
	}

	@Override
	public StatStringSet getStatStringSet(String name) {
		if ("ranges".equals(name)) {
			return ranges;
		} else if ("attackTypes".equals(name)) {
			return attackTypes;
		}
		return null;
	}

	@Override
	public int getValueInt(String name) {
		return switch (name) {
			case "damage" -> damage.value(getWeaponTemplate().getDamage(), 1, 1000);
			case "rate" -> rate.value(getWeaponTemplate().getRate(), 1, 50);
			case "critDamage" -> critDamage.value(getWeaponTemplate().getCritDamage(), 0, 1000);
			case "clipSize" -> clipSize.value(getWeaponTemplate().getClipSize(), 0, 100);
			case "ammoCount" -> ammoCount;
			default -> super.getValueInt(name);
		};
	}

	@Override
	public float getValueFloat(String name) {
		return switch (name) {
			case "accuracyBonus" -> accuracyBonus.value(getWeaponTemplate().getAccuracyBonus(), -1.0f, 1.0f);
			case "armorMult" -> armorMult.value(getWeaponTemplate().getArmorMult(), 0.0f, 2.0f);
			default -> super.getValueFloat(name);
		};
	}

	@Override
	public boolean getValueBoolean(String name) {
		if ("isSilenced".equals(name)) {
			return isSilenced.value(getWeaponTemplate().isSilenced());
		}
		return super.getValueBoolean(name);
	}

	@Override
	public String getValueString(String name) {
		if ("damageType".equals(name)) {
			return damageType.value(getWeaponTemplate().getDamageType().toString().toLowerCase());
		}
		return super.getValueString(name);
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return switch (name) {
			case "attackTypes" -> attackTypes.value(getWeaponClass().getAttackTypes());
			case "ranges" -> ranges.valueFromEnum(getWeaponClass().getPrimaryRanges());
			default -> super.getValueStringSet(name);
		};
	}

	@Override
	public void onStatChange() {
		if (ammoCount > getClipSize()) {
			int difference = ammoCount - getClipSize();
			ammoCount = getClipSize();
			if (getEquippedActor() != null) {
				getEquippedActor().getInventory().addItems(ammoType.getTemplate().getID(), difference);
			}
		}
	}

	@Override
	public void triggerEffect(String name) {}

	@Override
	public void modStateInteger(String name, int amount) {
		if ("ammo".equals(name)) {
			ammoCount = MathUtils.bound(ammoCount + amount, 0, getClipSize());
		} else {
			super.modStateInteger(name, amount);
		}
	}

	public void addEffect(Effect effect) {
		if (effect.isInstant()) {
			effect.start(this);
			effect.end(this);
		} else {
			if (!effects.containsKey(effect)) {
				effects.put(effect, new ArrayList<>());
			}
			if (effect.isStackable() || !effects.get(effect).isEmpty()) {
				effects.get(effect).add(0);
				effect.start(this);
			} else {
				effects.get(effect).set(0, 0);
			}
		}
	}

	public void removeEffect(Effect effect) {
		if (effects.containsKey(effect)) {
			effect.end(this);
			effects.get(effect).remove(0);
			if (effects.get(effect).isEmpty()) {
				effects.remove(effect);
			}
		}
	}

	@Override
	public void loadState(SaveData saveData) {
		if ("ammoCount".equals(saveData.getParameter())) {
			this.ammoCount = saveData.getValueInt();
		} else {
			super.loadState(saveData);
		}
	}

	@Override
	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if (ammoCount != getWeaponTemplate().getClipSize()) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "ammoCount", ammoCount));
		}
		return state;
	}

}
