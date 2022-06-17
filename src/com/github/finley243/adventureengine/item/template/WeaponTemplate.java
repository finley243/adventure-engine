package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeaponTemplate extends ItemTemplate {

	public static final float CRIT_CHANCE = 0.05f;

	public enum WeaponType {
		PISTOL(true, false, 0, 3, Set.of()),
		SMG(true, false, 0, 2, Set.of(AttackType.AUTO)),
		SHOTGUN(true, true, 1, 2, Set.of()),
		ASSAULT_RIFLE(true, true, 1, 4, Set.of(AttackType.AUTO)),
		SNIPER_RIFLE(true, true, 4, 15, Set.of()),
		KNIFE(false, false, 0, 0, Set.of()),
		SWORD(false, true, 0, 0, Set.of(AttackType.THRUST)),
		CLUB(false, true, 0, 0, Set.of()),
		AXE(false, true, 0, 0, Set.of(AttackType.SWEEP));
		
		public final boolean isRanged, isTwoHanded;
		public final int rangeMin, rangeMax;
		public final Set<AttackType> attacks;
		
		WeaponType(boolean isRanged, boolean isTwoHanded, int rangeMin, int rangeMax, Set<AttackType> attacks) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
			this.rangeMin = rangeMin;
			this.rangeMax = rangeMax;
			this.attacks = attacks;
		}
	}

	public enum AttackType {
		AUTO, SWEEP, THRUST
	}
	
	private final WeaponType type;
	private final int damage;
	private final int rate;
	private final int critDamage;
	private final int rangeMin;
	private final int rangeMax;
	private final int clipSize;
	private final float accuracyBonus;
	private final boolean silenced;
	private final String ammo;
	
	public WeaponTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, WeaponType type, int damage, int rate, int critDamage, int rangeMin, int rangeMax, int clipSize, float accuracyBonus, boolean silenced, String ammo) {
		super(ID, name, description, scripts, price);
		if(clipSize > 0 && ammo == null || clipSize == 0 && ammo != null) throw new IllegalArgumentException("Weapon clip size and ammo type conflict: " + ID);
		if(type == null) throw new IllegalArgumentException("Weapon type cannot be null: " + ID);
		this.type = type;
		this.damage = damage;
		this.rate = rate;
		this.critDamage = critDamage;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.clipSize = clipSize;
		this.accuracyBonus = accuracyBonus;
		this.silenced = silenced;
		this.ammo = ammo;
	}

	@Override
	public boolean hasState() {
		return true;
	}
	
	public WeaponType getType() {
		return type;
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

	public int getRangeMin() {
		return rangeMin;
	}

	public int getRangeMax() {
		return rangeMax;
	}
	
	public int getClipSize() {
		return clipSize;
	}

	public float getAccuracyBonus() {
		return accuracyBonus;
	}

	public boolean isSilenced() {
		return silenced;
	}

	public String getAmmo() {
		return ammo;
	}

	public Actor.Skill getSkill() {
		switch(getType()) {
			case PISTOL:
			case SMG:
				return Actor.Skill.HANDGUNS;
			case SHOTGUN:
			case ASSAULT_RIFLE:
			case SNIPER_RIFLE:
				return Actor.Skill.LONG_ARMS;
			case KNIFE:
			case SWORD:
			case CLUB:
			case AXE:
				return Actor.Skill.MELEE;
		}
		return null;
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("weapon");
		if(getType().isRanged) {
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
		}
		return tags;
	}
	
}
