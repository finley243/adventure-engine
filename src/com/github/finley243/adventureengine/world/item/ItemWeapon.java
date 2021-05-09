package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionAttackMelee;
import com.github.finley243.adventureengine.action.ActionAttackRanged;
import com.github.finley243.adventureengine.action.ActionEquip;
import com.github.finley243.adventureengine.action.ActionUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.template.StatsWeapon;

public class ItemWeapon extends Item {
	
	private StatsWeapon stats;
	
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
	
	public int getDamage() {
		return stats.getDamage();
	}
	
	public float getHitChance() {
		return stats.getHitChance();
	}
	
	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		if(!subject.hasEquippedItem()) {
			actions.add(new ActionEquip(this));
		}
		return actions;
	}
	
	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		Set<Actor> targets = stats.getType().isRanged ? subject.getArea().getRoom().getActors() : subject.getArea().getActors();
		for(Actor target : targets) {
			if(target != subject) {
				if(stats.getType().isRanged && !target.isInCover()) {
					if(!subject.isInCover()) {
						actions.add(new ActionAttackRanged(this, target));
					}
				} else {
					actions.add(new ActionAttackMelee(this, target));
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
