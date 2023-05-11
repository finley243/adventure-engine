package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeaponTemplate extends ItemTemplate {

	private final String weaponClass;
	private final int damage;
	private final int rate;
	private final int critDamage;
	private final float critChance;
	private final int clipSize;
	private final float accuracyBonus;
	private final float armorMult;
	private final boolean silenced;
	private final Damage.DamageType damageType;

	public WeaponTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, int price, String weaponClass, int damage, int rate, int critDamage, float critChance, int clipSize, float accuracyBonus, float armorMult, boolean silenced, Damage.DamageType damageType) {
		super(game, ID, name, description, scripts, price);
		if (weaponClass == null) throw new IllegalArgumentException("Weapon class cannot be null: " + ID);
		this.weaponClass = weaponClass;
		this.damage = damage;
		this.rate = rate;
		this.critDamage = critDamage;
		this.critChance = critChance;
		this.clipSize = clipSize;
		this.accuracyBonus = accuracyBonus;
		this.armorMult = armorMult;
		this.silenced = silenced;
		this.damageType = damageType;
	}

	@Override
	public boolean hasState() {
		return true;
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

	public float getAccuracyBonus() {
		return accuracyBonus;
	}

	public float getArmorMult() {
		return armorMult;
	}

	public boolean isSilenced() {
		return silenced;
	}

	public Damage.DamageType getDamageType() {
		return damageType;
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
