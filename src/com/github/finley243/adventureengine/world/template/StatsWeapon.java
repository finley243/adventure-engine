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
	
	private WeaponType type;
	private int actionPoints;
	private int damage;
	private float hitChance;
	private int clipSize;
	
	public StatsWeapon(String ID, String name, int price, WeaponType type, int actionPoints, int damage, float hitChance, int clipSize) {
		super(ID, name, price);
		this.type = type;
		this.actionPoints = actionPoints;
		this.damage = damage;
		this.hitChance = hitChance;
		this.clipSize = clipSize;
	}
	
	public WeaponType getType() {
		return type;
	}
	
	public int getActionPoints() {
		return actionPoints;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public float getHitChance() {
		return hitChance;
	}
	
	public int getClipSize() {
		return clipSize;
	}
	
}
