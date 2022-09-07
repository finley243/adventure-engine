package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeaponTemplate extends ItemTemplate {

	public static final float CRIT_CHANCE = 0.05f;

	public enum AttackType {
		AUTO, SWEEP, THRUST
	}
	
	private final String weaponClass;
	private final int damage;
	private final int rate;
	private final int critDamage;
	private final int clipSize;
	private final float accuracyBonus;
	private final float armorMult;
	private final boolean silenced;
	private final Damage.DamageType damageType;

	public WeaponTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, String weaponClass, int damage, int rate, int critDamage, int clipSize, float accuracyBonus, float armorMult, boolean silenced, Damage.DamageType damageType) {
		super(ID, name, description, scripts, price);
		if(weaponClass == null) throw new IllegalArgumentException("Weapon class cannot be null: " + ID);
		this.weaponClass = weaponClass;
		this.damage = damage;
		this.rate = rate;
		this.critDamage = critDamage;
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
	
	public String getWeaponClass() {
		return weaponClass;
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
		// TODO - Rewrite weapon tag system for WeaponClass system
		/*if(getType().isRanged) {
			tags.add("weapon_ranged");
		} else {
			tags.add("weapon_melee");
		}
		switch(getSkill()) {
			case HANDGUNS:
				tags.add("weapon_handgun");
				break;
			case LONG_ARMS:
				tags.add("weapon_long_arm");
				break;
		}
		switch(getType()) {
			case PISTOL:
				tags.add("weapon_pistol");
				break;
			case SMG:
				tags.add("weapon_smg");
				break;
			case SHOTGUN:
				tags.add("weapon_shotgun");
				break;
			case ASSAULT_RIFLE:
				tags.add("weapon_assault_rifle");
				break;
			case SNIPER_RIFLE:
				tags.add("weapon_sniper_rifle");
				break;
			case KNIFE:
				tags.add("weapon_knife");
				break;
			case SWORD:
				tags.add("weapon_sword");
				break;
			case CLUB:
				tags.add("weapon_club");
				break;
			case AXE:
				tags.add("weapon_axe");
				break;
		}*/
		return tags;
	}
	
}
