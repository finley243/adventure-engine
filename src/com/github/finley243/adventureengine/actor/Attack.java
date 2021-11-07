package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class Attack {

	public enum AttackType {
		NORMAL, AIMED
	}
	
	private final Actor subject;
	private final Actor target;
	private final ItemWeapon weapon;
	private final AttackType type;
	
	public Attack(Actor subject, Actor target, ItemWeapon weapon, AttackType type) {
		this.subject = subject;
		this.target = target;
		this.weapon = weapon;
		this.type = type;
	}
	
	public Actor getSubject() {
		return subject;
	}
	
	public Actor getTarget() {
		return target;
	}
	
	public ItemWeapon getWeapon() {
		return weapon;
	}
	
	public AttackType getType() {
		return type;
	}

}
