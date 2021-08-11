package com.github.finley243.adventureengine.world.item;

import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionAttackMelee;
import com.github.finley243.adventureengine.action.ActionAttackRanged;
import com.github.finley243.adventureengine.action.ActionEquip;
import com.github.finley243.adventureengine.action.ActionInspect;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.action.ActionReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.template.StatsWeapon;

public class ItemWeapon extends ItemEquippable {
	
	private StatsWeapon stats;
	private int ammo;
	
	public ItemWeapon(StatsWeapon stats) {
		super(stats.getName());
		this.stats = stats;
	}
	
	@Override
	public String getDescription() {
		return stats.getDescription();
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
	
	public int getRate() {
		return stats.getRate();
	}
	
	public float getHitChance(Actor subject) {
		return stats.getHitChance();
	}
	
	public void reloadFull() {
		ammo = stats.getClipSize();
	}

	public float getAmmoFraction() {
		return ((float) ammo) / ((float) stats.getClipSize());
	}
	
	public void consumeAmmo(int amount) {
		ammo -= amount;
	}
	
	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		if(!subject.hasEquippedItem()) {
			actions.add(0, new ActionEquip(this));
		}
		return actions;
	}
	
	@Override
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = super.equippedActions(subject);
		Set<Actor> targets = stats.getType().isRanged ? subject.getArea().getRoom().getActors() : subject.getArea().getActors();
		for(Actor target : targets) {
			if(target != subject) {
				if(stats.getType().isRanged) { // Ranged
					if(!target.isIncapacitated() && (!target.isInCover() || target.getArea().equals(subject.getArea())) && ammo > 0) {
						actions.add(new ActionAttackRanged(this, target));
					}
				} else { // Melee
					if(!target.isIncapacitated()) {
						actions.add(new ActionAttackMelee(this, target));
					}
				}
			}
		}
		if(ammo < stats.getClipSize()) { // Add check to see if subject has ammo
			actions.add(new ActionReload(this));
		}
		if(this.getDescription() != null) {
			actions.add(new ActionInspect(this, InspectType.EQUIPPED));
		}
		return actions;
	}
	
	@Override
	public boolean equalsInventory(Item o) {
		if(!(o instanceof ItemWeapon)) {
			return false;
		} else {
			ItemWeapon other = (ItemWeapon) o;
			return this.stats == other.stats;
		}
	}

}
