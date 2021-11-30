package com.github.finley243.adventureengine.world.template;

public class StatsWeapon extends StatsItem {
	
	public enum WeaponType{
		PISTOL(true, false),
		SMG(true, false),
		SHOTGUN(true, true),
		ASSAULT_RIFLE(true, true),
		SNIPER_RIFLE(true, true),
		KNIFE(false, false),
		SWORD(false, true),
		CLUB(false, true),
		AXE(false, true);
		
		public final boolean isRanged, isTwoHanded;
		
		WeaponType(boolean isRanged, boolean isTwoHanded) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
		}
	}
	
	private final WeaponType type;
	private final int damage;
	private final int rate;
	private final int critDamage;
	private final int range;
	private final int clipSize;
	
	public StatsWeapon(String ID, String name, String description, int price, WeaponType type, int damage, int rate, int critDamage, int range, int clipSize) {
		super(ID, name, description, price);
		this.type = type;
		this.damage = damage;
		this.rate = rate;
		this.critDamage = critDamage;
		this.range = range;
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

	public int getRange() {
		return range;
	}
	
	public int getClipSize() {
		return clipSize;
	}
	
}
