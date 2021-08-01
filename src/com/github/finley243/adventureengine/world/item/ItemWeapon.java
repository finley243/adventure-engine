package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionAttackMelee;
import com.github.finley243.adventureengine.action.ActionAttackRanged;
import com.github.finley243.adventureengine.action.ActionEquip;
import com.github.finley243.adventureengine.action.ActionReload;
import com.github.finley243.adventureengine.action.ActionUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.template.StatsWeapon;

public class ItemWeapon extends Item {
	
	private StatsWeapon stats;
	private int ammo;
	
	public ItemWeapon(StatsWeapon stats) {
		super(stats.getName());
		this.stats = stats;
	}
	
	@Override
	public int getPrice() {
		return stats.getPrice();
	}
	
	@Override
	public String getID() {
		return stats.getID();
	}
	
	public boolean isMelee() {
		return !stats.getType().isRanged;
	}
	
	public int getDamage() {
		return stats.getDamage();
	}
	
	public float getHitChance(Actor subject) {
		return stats.getHitChance();
	}
	
	public void reloadFull() {
		ammo = stats.getClipSize();
	}
	
	public void consumeAmmo(int amount) {
		ammo -= amount;
	}
	
	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		if(!subject.hasEquippedItem()) {
			actions.add(new ActionEquip(this));
		}
		actions.addAll(super.inventoryActions(subject));
		return actions;
	}
	
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		Set<Actor> targets = stats.getType().isRanged ? subject.getArea().getRoom().getActors() : subject.getArea().getActors();
		for(Actor target : targets) {
			if(target != subject) {
				if(stats.getType().isRanged) { // Ranged
					if(!target.isIncapacitated() && (!target.isInCover() || target.getArea().equals(subject.getArea())) && !subject.isInCover() && ammo > 0) {
						actions.add(new ActionAttackRanged(this, target));
					}
					if(ammo < stats.getClipSize()) { // Add check to see if subject has ammo
						actions.add(new ActionReload(this));
					}
				} else { // Melee
					if(!target.isIncapacitated()) {
						actions.add(new ActionAttackMelee(this, target));
					}
				}
			}
		}
		actions.add(new ActionUnequip(this));
		return actions;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemWeapon)) {
			return false;
		} else {
			ItemWeapon other = (ItemWeapon) o;
			return this.stats == other.stats;
		}
	}

}
