package com.github.finley243.adventureengine.world.template;

public class StatsWeapon extends StatsItem {
	
	public enum WeaponType{
		PISTOL(true, false, false),
		SMG(true, false, true),
		SHOTGUN(true, true, false),
		ASSAULT_RIFLE(true, true, true),
		SNIPER_RIFLE(true, true, false),
		KNIFE(false, false, false),
		SWORD(false, true, false),
		CLUB(false, true, false),
		AXE(false, true, false);
		
		public final boolean isRanged, isTwoHanded, hasAuto;
		
		WeaponType(boolean isRanged, boolean isTwoHanded, boolean hasAuto) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
			this.hasAuto = hasAuto;
		}
	}
	
	private final WeaponType type;
	private final int damage;
	private final int rate;
	private final int critDamage;
	private final int rangeMin;
	private final int rangeMax;
	private final int clipSize;
	
	public StatsWeapon(String ID, String name, String description, int price, WeaponType type, int damage, int rate, int critDamage, int rangeMin, int rangeMax, int clipSize) {
		super(ID, name, description, price);
		this.type = type;
		this.damage = damage;
		this.rate = rate;
		this.critDamage = critDamage;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.clipSize = clipSize;
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
	
}
