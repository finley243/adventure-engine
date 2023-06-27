package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.*;

public class WeaponTemplate extends EquippableTemplate {

	private final String weaponClass;
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
	private final Map<String, Integer> modSlots;

	public WeaponTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, List<ActionCustom.CustomActionHolder> customActions, int price, List<ActionCustom.CustomActionHolder> equippedActions, String weaponClass, int damage, int rate, int critDamage, float critChance, int clipSize, int reloadActionPoints, float armorMult, boolean silenced, String damageType, Set<String> targetEffects, Map<String, Integer> modSlots) {
		super(game, ID, name, description, scripts, customActions, price, null, new ArrayList<>(), equippedActions);
		if (weaponClass == null) throw new IllegalArgumentException("Weapon class cannot be null: " + ID);
		for (Map.Entry<String, Integer> entry : modSlots.entrySet()) {
			if (entry.getValue() <= 0) {
				throw new IllegalArgumentException("Specified mod slot must have a count greater than or equal to 1: " + ID + " - slot: " + entry.getKey());
			}
		}
		this.weaponClass = weaponClass;
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
		this.modSlots = modSlots;
	}

	@Override
	public Set<Set<String>> getSlots() {
		Set<Set<String>> slots = new HashSet<>();
		if (getWeaponClass().isTwoHanded()) {
			Set<String> bothHands = new HashSet<>();
			bothHands.add("hand_main");
			bothHands.add("hand_off");
			slots.add(bothHands);
		} else {
			Set<String> mainHand = new HashSet<>();
			Set<String> offHand = new HashSet<>();
			mainHand.add("hand_main");
			offHand.add("hand_off");
			slots.add(mainHand);
			slots.add(offHand);
		}
		return slots;
	}
	
	public WeaponClass getWeaponClass() {
		return game().data().getWeaponClass(weaponClass);
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getRate() {
		return rate;
	}
	
	public int getCritDamage() {
		return critDamage;
	}

	public float getCritChance() {
		return critChance;
	}
	
	public int getClipSize() {
		return clipSize;
	}

	public int getReloadActionPoints() {
		return reloadActionPoints;
	}

	public float getArmorMult() {
		return armorMult;
	}

	public boolean isSilenced() {
		return silenced;
	}

	public String getDamageType() {
		return damageType;
	}

	public Set<String> getTargetEffects() {
		return targetEffects;
	}

	public Map<String, Integer> getModSlots() {
		return modSlots;
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("weapon");
		WeaponClass weaponClassInstance = getWeaponClass();
		if (weaponClassInstance.isRanged()) {
			tags.add("weapon_ranged");
		} else {
			tags.add("weapon_melee");
		}
		if (weaponClassInstance.isTwoHanded()) {
			tags.add("weapon_two_handed");
		} else {
			tags.add("weapon_one_handed");
		}
		tags.add("weapon_class_" + weaponClassInstance.getID());
		tags.add("weapon_skill_" + weaponClassInstance.getSkill().toString().toLowerCase());
		return tags;
	}
	
}
