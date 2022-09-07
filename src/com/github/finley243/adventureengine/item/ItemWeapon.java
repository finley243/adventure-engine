package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.action.attack.ActionAttackAreaWeapon;
import com.github.finley243.adventureengine.action.attack.ActionAttackBasicWeapon;
import com.github.finley243.adventureengine.action.attack.ActionAttackLimbWeapon;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.moddable.*;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.WeaponAttackType;
import com.github.finley243.adventureengine.item.template.WeaponClass;
import com.github.finley243.adventureengine.item.template.WeaponTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.*;

public class ItemWeapon extends ItemEquippable implements Moddable {

	public static final float HIT_CHANCE_BASE_MELEE_MIN = 0.10f;
	public static final float HIT_CHANCE_BASE_MELEE_MAX = 0.90f;
	public static final float HIT_CHANCE_BASE_RANGED_MIN = 0.10f;
	public static final float HIT_CHANCE_BASE_RANGED_MAX = 0.90f;
	
	private final WeaponTemplate stats;
	private final ModdableStatInt damage;
	private final ModdableStatInt rate;
	private final ModdableStatInt critDamage;
	private final ModdableStatString range;
	private final ModdableStatInt clipSize;
	private final ModdableStatFloat accuracyBonus;
	private final ModdableStatFloat armorMult;
	private final ModdableStatString damageType;
	private final ModdableStatBoolean isSilenced;
	private final ModdableStatBoolean isPrimarySpread;
	private ItemAmmo ammoType;
	private int ammoCount;

	private final Map<Effect, List<Integer>> effects;
	
	public ItemWeapon(Game game, String ID, WeaponTemplate stats) {
		super(game, ID);
		this.stats = stats;
		this.damage = new ModdableStatInt(this);
		this.rate = new ModdableStatInt(this);
		this.critDamage = new ModdableStatInt(this);
		this.range = new ModdableStatString(this);
		this.clipSize = new ModdableStatInt(this);
		this.accuracyBonus = new ModdableStatFloat(this);
		this.armorMult = new ModdableStatFloat(this);
		this.damageType = new ModdableStatString(this);
		this.isSilenced = new ModdableStatBoolean(this, false);
		this.isPrimarySpread = new ModdableStatBoolean(this, true);
		this.ammoType = null;
		this.ammoCount = 0;
		this.effects = new HashMap<>();
	}

	@Override
	public ItemTemplate getTemplate() {
		return stats;
	}

	public WeaponClass getWeaponClass() {
		return game().data().getWeaponClass(stats.getWeaponClass());
	}
	
	public boolean isRanged() {
		return getWeaponClass().isRanged();
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
		return range.valueEnum(getWeaponClass().getPrimaryRange(), AreaLink.DistanceCategory.class);
	}

	// TODO - Change to accuracy multiplier?
	public float getAccuracyBonus() {
		return accuracyBonus.value(stats.getAccuracyBonus(), -1.0f, 1.0f);
	}

	public float getArmorMult() {
		return armorMult.value(stats.getArmorMult(), 0.0f, 2.0f);
	}

	public int getClipSize() {
		return clipSize.value(stats.getClipSize(), 0, 100);
	}

	public Damage.DamageType getDamageType() {
		return damageType.valueEnum(stats.getDamageType(), Damage.DamageType.class);
	}

	public int getAmmoRemaining() {
		return ammoCount;
	}

	public float getAmmoFraction() {
		if(stats.getClipSize() == 0) return 1.0f;
		return ((float) ammoCount) / ((float) stats.getClipSize());
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
		return isSilenced.value(stats.isSilenced());
	}

	public Set<String> getAmmoTypes() {
		return getWeaponClass().getAmmoTypes();
	}

	public Actor.Skill getSkill() {
		return getWeaponClass().getSkill();
	}

	public String getHitPhrase() {
		return getWeaponClass().getHitPhrase();
	}

	public String getHitRepeatPhrase() {
		return getWeaponClass().getHitPhraseRepeat();
	}

	public String getMissPhrase() {
		return getWeaponClass().getMissPhrase();
	}

	public String getMissRepeatPhrase() {
		return getWeaponClass().getMissPhraseRepeat();
	}

	public String getLimbHitPhrase() {
		return getWeaponClass().getLimbHitPhrase();
	}

	public String getLimbHitRepeatPhrase() {
		return getWeaponClass().getLimbHitPhraseRepeat();
	}

	public String getLimbMissPhrase() {
		return getWeaponClass().getLimbMissPhrase();
	}

	public String getLimbMissRepeatPhrase() {
		return getWeaponClass().getLimbMissPhraseRepeat();
	}

	@Override
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = super.equippedActions(subject);
		if (isPrimarySpread.value(false)) {
			for (Area targetArea : subject.getArea().getVisibleAreas(subject)) {
				actions.add(new ActionAttackAreaWeapon(this, targetArea, "Attack"));
			}
		} else {
			for (AttackTarget target : subject.getVisibleAttackTargets()) {
				if (target instanceof Actor){
					if (!target.equals(subject) && !((Actor) target).isDead()) {
						actions.add(new ActionAttackBasicWeapon(this, target, "Attack"));
						for (Limb limb : ((Actor) target).getLimbs()) {
							actions.add(new ActionAttackLimbWeapon(this, target, limb, "Targeted Attack"));
						}
					}
				} else {
					actions.add(new ActionAttackBasicWeapon(this, target, "Attack"));
				}
			}
		}
		for (WeaponAttackType secondaryAttack : getWeaponClass().getAttackTypes()) {
			actions.addAll(secondaryAttack.generateActions(subject, this));
		}
		if (getClipSize() > 0) {
			for (String current : getWeaponClass().getAmmoTypes()) {
				actions.add(new ActionWeaponReload(this, (ItemAmmo) ItemFactory.create(game(), current)));
			}
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
		} else if ("armorMult".equals(name)) {
			return armorMult;
		}
		return null;
	}

	@Override
	public ModdableStatBoolean getStatBoolean(String name) {
		if ("isSilenced".equals(name)) {
			return isSilenced;
		} else if ("isPrimarySpread".equals(name)) {
			return isPrimarySpread;
		}
		return null;
	}

	@Override
	public ModdableEffectList getStatEffects(String name) {
		return null;
	}

	@Override
	public ModdableStatString getStatString(String name) {
		if ("range".equals(name)) {
			return range;
		} else if ("damageType".equals(name)) {
			return damageType;
		}
		return null;
	}

	@Override
	public ModdableStringSet getStatStrings(String name) {
		return null;
	}

	@Override
	public void onStatChange() {
		if(ammoCount > getClipSize()) {
			int difference = ammoCount - getClipSize();
			ammoCount = getClipSize();
			// TODO - Return ammo items to actor
		}
	}

	@Override
	public void modifyState(String name, int amount) {
		if ("ammo".equals(name)) {
			ammoCount = MathUtils.bound(ammoCount + amount, 0, getClipSize());
		}
	}

	@Override
	public void triggerEffect(String name) {}

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
		if(effects.containsKey(effect)) {
			effect.end(this);
			effects.get(effect).remove(0);
			if(effects.get(effect).isEmpty()) {
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
		if(ammoCount != stats.getClipSize()) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "ammoCount", ammoCount));
		}
		return state;
	}

}
