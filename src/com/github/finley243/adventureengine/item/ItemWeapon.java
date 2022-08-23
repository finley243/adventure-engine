package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.action.attack.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.effect.moddable.*;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.WeaponTemplate;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ItemWeapon extends ItemEquippable implements Moddable {

	public static final float HIT_CHANCE_BASE_MELEE_MIN = 0.10f;
	public static final float HIT_CHANCE_BASE_MELEE_MAX = 0.80f;
	public static final float HIT_CHANCE_BASE_RANGED_MIN = 0.10f;
	public static final float HIT_CHANCE_BASE_RANGED_MAX = 0.90f;
	
	private final WeaponTemplate stats;
	private final ModdableStatInt damage;
	private final ModdableStatInt rate;
	private final ModdableStatInt critDamage;
	private final ModdableStatEnum<AreaLink.DistanceCategory> range;
	private final ModdableStatInt clipSize;
	private final ModdableStatFloat accuracyBonus;
	private int ammo;
	
	public ItemWeapon(Game game, String ID, WeaponTemplate stats) {
		super(game, ID);
		this.stats = stats;
		this.damage = new ModdableStatInt(this);
		this.rate = new ModdableStatInt(this);
		this.critDamage = new ModdableStatInt(this);
		this.range = new ModdableStatEnum<>(this, AreaLink.DistanceCategory.class);
		this.clipSize = new ModdableStatInt(this);
		this.accuracyBonus = new ModdableStatFloat(this);
		this.ammo = stats.getClipSize();
	}

	@Override
	public ItemTemplate getTemplate() {
		return stats;
	}
	
	public boolean isRanged() {
		return stats.getType().isRanged;
	}
	
	public int getDamage() {
		return damage.value(stats.getDamage(), 1, 1000);
	}
	
	public int getRate() {
		return rate.value(stats.getRate(), 1, 50);
	}

	public float getBaseHitChanceMin() {
		return isRanged() ? HIT_CHANCE_BASE_RANGED_MIN : HIT_CHANCE_BASE_MELEE_MIN;
	}

	public float getBaseHitChanceMax() {
		return isRanged() ? HIT_CHANCE_BASE_RANGED_MAX : HIT_CHANCE_BASE_MELEE_MAX;
	}
	
	public int getCritDamage() {
		return critDamage.value(stats.getCritDamage(), 0, 1000);
	}

	public AreaLink.DistanceCategory getRange() {
		return range.value(stats.getRange());
	}

	// TODO - Change to accuracy multiplier?
	public float getAccuracyBonus() {
		return accuracyBonus.value(stats.getAccuracyBonus(), 0.0f, 1.0f);
	}

	public int getClipSize() {
		return clipSize.value(stats.getClipSize(), 0, 100);
	}

	public int getAmmoRemaining() {
		return ammo;
	}

	public float getAmmoFraction() {
		if(stats.getClipSize() == 0) return 1.0f;
		return ((float) ammo) / ((float) stats.getClipSize());
	}

	public int reloadCapacity() {
		return getClipSize() - getAmmoRemaining();
	}

	public void loadAmmo(int amount) {
		ammo += amount;
	}
	
	public void consumeAmmo(int amount) {
		ammo -= amount;
	}

	public boolean isSilenced() {
		return stats.isSilenced();
	}

	public String getAmmoType() {
		return stats.getAmmo();
	}

	public Actor.Skill getSkill() {
		return stats.getSkill();
	}

	@Override
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = super.equippedActions(subject);
		for(Actor target : subject.getVisibleActors()) {
			if(target != subject && !target.isDead()) {
				if(stats.getType().isRanged) { // Ranged
					actions.add(new ActionAttackBasic(this, target, "Attack", "rangedHit", "rangedHitRepeat", "rangedMiss", "rangedMissRepeat", 1, false, 1.0f, 0.0f));
					for(Limb limb : target.getLimbs()) {
						actions.add(new ActionAttackLimb(this, target, limb, "Targeted Attack", "rangedHitLimb", "rangedHitLimbRepeat", "rangedMissLimb", "rangedMissLimbRepeat", 1, true, 1.0f, 0.0f));
					}
					if(stats.getType().attacks.contains(WeaponTemplate.AttackType.AUTO)) {
						actions.add(new ActionAttackBasic(this, target, "Autofire", "rangedAutoHit", "rangedAutoHit", "rangedAutoMiss", "rangedAutoMiss", 6, true, 3.0f, -0.5f));
					}
				} else { // Melee
					actions.add(new ActionAttackBasic(this, target, "Attack", "meleeHit", "meleeHitRepeat", "meleeMiss", "meleeMissRepeat", 1, false, 1.0f, 0.0f));
					for(Limb limb : target.getLimbs()) {
						actions.add(new ActionAttackLimb(this, target, limb, "Targeted Attack", "meleeHitLimb", "meleeHitLimbRepeat", "meleeMissLimb", "meleeMissLimbRepeat", 1, true, 1.0f, 0.0f));
					}
				}
			}
		}
		if(getClipSize() > 0) {
			actions.add(new ActionWeaponReload(this));
		}
		return actions;
	}

	@Override
	public ModdableStatInt getStatInt(String name) {
		switch(name) {
			case "damage":
				return damage;
			case "rate":
				return rate;
			case "critDamage":
				return critDamage;
			case "clipSize":
				return clipSize;
			default:
				return null;
		}
	}

	@Override
	public ModdableStatFloat getStatFloat(String name) {
		if ("accuracyBonus".equals(name)) {
			return accuracyBonus;
		}
		return null;
	}

	@Override
	public ModdableStatBoolean getStatBoolean(String name) {
		return null;
	}

	@Override
	public ModdableEffectList getStatEffects(String name) {
		return null;
	}

	@Override
	public void onStatChange() {
		if(ammo > getClipSize()) {
			ammo = getClipSize();
		}
	}

	@Override
	public void modifyState(String name, int amount) {
		if ("ammo".equals(name)) {
			ammo = MathUtils.bound(ammo + amount, 0, getClipSize());
		}
	}

	@Override
	public void triggerEffect(String name) {}

	@Override
	public void loadState(SaveData saveData) {
		if ("ammo".equals(saveData.getParameter())) {
			this.ammo = saveData.getValueInt();
		} else {
			super.loadState(saveData);
		}
	}

	@Override
	public List<SaveData> saveState() {
		List<SaveData> state = super.saveState();
		if(ammo != stats.getClipSize()) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "ammo", ammo));
		}
		return state;
	}

}
