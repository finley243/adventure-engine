package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.action.ActionInspect.InspectType;
import com.github.finley243.adventureengine.action.ActionReactionOld.ReactionType;
import com.github.finley243.adventureengine.action.attack.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.template.StatsWeapon;

public class ItemWeapon extends ItemEquippable {
	
	public static final float CRIT_CHANCE = 0.05f;
	
	private final StatsWeapon stats;
	private int ammo;
	
	public ItemWeapon(StatsWeapon stats) {
		super(stats.generateInstanceID(), stats.getName());
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
	public String getStatsID() {
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

	public int getRangeMin() {
		return stats.getRangeMin();
	}

	public int getRangeMax() {
		return stats.getRangeMax();
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

	public Actor.Skill getSkill() {
		switch(stats.getType()) {
			case PISTOL:
			case SMG:
				return Actor.Skill.HANDGUNS;
			case SHOTGUN:
			case ASSAULT_RIFLE:
			case SNIPER_RIFLE:
				return Actor.Skill.LONG_ARMS;
			case KNIFE:
			case SWORD:
			case CLUB:
			case AXE:
				return Actor.Skill.MELEE;
		}
		return null;
	}
	
	public List<Action> reactionActions(Actor target) {
		List<Action> actions = new ArrayList<>();
		if(!isRanged()) {
			actions.add(new ActionReactionOld(ReactionType.BLOCK));
			actions.add(new ActionReactionOld(ReactionType.DODGE));
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
		for(Actor target : subject.getVisibleActors()) {
			if(target != subject && target.isActive()) {
				if(stats.getType().isRanged) { // Ranged
					actions.add(new ActionRangedAttack(this, target));
					for(Limb limb : target.getLimbs()) {
						actions.add(new ActionRangedAttackTargeted(this, target, limb));
					}
					if(stats.getType().hasAuto) {
						actions.add(new ActionRangedAttackAuto(this, target));
					}
				} else { // Melee
					actions.add(new ActionMeleeAttack(this, target));
					for(Limb limb : target.getLimbs()) {
						actions.add(new ActionMeleeAttackTargeted(this, target, limb));
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

}
