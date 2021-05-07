package com.github.finley243.adventureengine.world.template;

public class StatsWeapon extends StatsItem {
	
	public enum WeaponType{
		PISTOL(true, false, 2),
		SMG(true, false, 2),
		SHOTGUN(true, true, 1),
		ASSAULT_RIFLE(true, true, 2),
		SNIPER_RIFLE(true, true, 1),
		KNIFE(false, false, 2),
		SWORD(false, true, 1),
		CLUB(false, true, 1),
		AXE(false, true, 1);
		
		public final boolean isRanged, isTwoHanded;
		public final int rate;
		
		WeaponType(boolean isRanged, boolean isTwoHanded, int rate) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
			this.rate = rate;
		}
	}
	
	private WeaponType type;
	
	public StatsWeapon(String ID, String name, int price) {
		super(ID, name, price);
	}
	
	public WeaponType getType() {
		return type;
	}
	
}
