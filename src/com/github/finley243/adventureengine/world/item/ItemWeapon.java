package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.action.ActionReaction.ReactionType;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.world.template.StatsWeapon;

public class ItemWeapon extends ItemEquippable {
	
	public static final float CRIT_CHANCE = 0.05f;
	
	private final StatsWeapon stats;
	private int ammo;
	
	public ItemWeapon(StatsWeapon stats) {
		super(stats.getName());
		this.stats = stats;
		reloadFull();
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
	
	public boolean isRanged() {
		return stats.getType().isRanged;
	}
	
	public int getDamage() {
		return stats.getDamage();
	}
	
	public int getRate() {
		return stats.getRate();
	}
	
	public int getCritDamage() {
		return stats.getCritDamage();
	}
	
	public float getHitChance(Actor subject) {
		return stats.getHitChance();
	}
	
	public void reloadFull() {
		ammo = stats.getClipSize();
	}

	public int getAmmoRemaining() {
		return ammo;
	}

	public float getAmmoFraction() {
		return ((float) ammo) / ((float) stats.getClipSize());
	}
	
	public void consumeAmmo(int amount) {
		ammo -= amount;
	}
	
	public void attack(Actor subject, Actor target, Limb limb) {
		if(isRanged()) {
			consumeAmmo(1);
			Game.EVENT_BUS.post(new SoundEvent(subject.getArea(), true));
		}
		CombatHelper.handleAttack(subject, target, limb, this);
	}
	
	public List<Action> reactionActions(Actor target) {
		List<Action> actions = new ArrayList<>();
		if(!isRanged() && target.hasMeleeWeaponEquipped()) {
			actions.add(new ActionReaction(ReactionType.BLOCK));
		}
		if(!isRanged()) {
			actions.add(new ActionReaction(ReactionType.DODGE));
		}
		return actions;
	}
	
	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		if(!subject.hasEquippedItem()) {
			actions.add(0, new ActionItemEquip(this));
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
					if(target.isActive() && (!target.isInCover() || target.getArea().equals(subject.getArea())) && ammo > 0) {
						actions.add(new ActionWeaponAttack(this, target));
						for(Limb limb : target.getLimbs()) {
							actions.add(new ActionWeaponAttackTargeted(this, target, limb));
						}
					}
				} else { // Melee
					if(target.isActive()) {
						actions.add(new ActionWeaponAttack(this, target));
						for(Limb limb : target.getLimbs()) {
							actions.add(new ActionWeaponAttackTargeted(this, target, limb));
						}
					}
				}
			}
		}
		actions.add(new ActionWeaponReload(this));
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
