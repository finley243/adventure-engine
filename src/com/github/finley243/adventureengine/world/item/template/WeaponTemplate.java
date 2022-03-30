package com.github.finley243.adventureengine.world.item.template;

import com.github.finley243.adventureengine.script.Script;

import java.util.Map;
import java.util.Set;

public class WeaponTemplate extends ItemTemplate {
	
	public enum WeaponType {
		PISTOL(true, false, Set.of()),
		SMG(true, false, Set.of(AttackType.AUTO)),
		SHOTGUN(true, true, Set.of()),
		ASSAULT_RIFLE(true, true, Set.of(AttackType.AUTO)),
		SNIPER_RIFLE(true, true, Set.of()),
		KNIFE(false, false, Set.of()),
		SWORD(false, true, Set.of(AttackType.THRUST)),
		CLUB(false, true, Set.of()),
		AXE(false, true, Set.of(AttackType.SWEEP));
		
		public final boolean isRanged, isTwoHanded;
		public final Set<AttackType> attacks;
		
		WeaponType(boolean isRanged, boolean isTwoHanded, Set<AttackType> attacks) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
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
	
	public WeaponTemplate(String ID, String name, String description, Map<String, Script> scripts, int price, WeaponType type, int damage, int rate, int critDamage, int rangeMin, int rangeMax, int clipSize, float accuracyBonus, boolean silenced) {
		super(ID, name, description, scripts, price);
		this.type = type;
		this.damage = damage;
		this.rate = rate;
		this.critDamage = critDamage;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.clipSize = clipSize;
		this.accuracyBonus = accuracyBonus;
		this.silenced = silenced;
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
	
}
