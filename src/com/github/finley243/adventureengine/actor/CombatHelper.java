package com.github.finley243.adventureengine.actor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionReaction;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class CombatHelper {

	public static final float HIT_CHANCE_MAX = 0.85f;
	public static final float HIT_CHANCE_MIN = 0.15f;
	public static final float RANGE_PENALTY = 0.10f;
	public static final float RANGE_PENALTY_MAX = 0.50f;

	public static final float BLOCK_CHANCE = 0.3f;
	public static final float DODGE_CHANCE = 0.4f;

	public static Context lastAttack;

	public static void newTurn() {
		lastAttack = null;
	}

	public static void handleAttack(Actor subject, Actor target, Limb limb, ItemWeapon weapon) {
		boolean isRepeat = lastAttack != null && lastAttack.getSubject() == subject && lastAttack.getObject() == target && lastAttack.getObject2() == weapon;
		target.addCombatTarget(subject);
		Context attackContext = new Context(subject, false, target, false, weapon, false);
		if(!isRepeat) {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(weapon.isRanged() ? "rangedTelegraph" : "meleeTelegraph"), attackContext, null, null));
		}
		if(ThreadLocalRandom.current().nextFloat() < calculateHitChance(subject, target, limb, weapon)) {
			handleHit(subject, target, limb, weapon);
		} else {
			if(limb == null) {
				Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(weapon.isRanged() ? "rangedMiss" : "meleeMiss"), attackContext, null, null));
			} else {
				Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(weapon.isRanged() ? limb.getRangedMissPhrase() : limb.getMeleeMissPhrase()), attackContext, null, null));
			}
		}
		lastAttack = attackContext;
	}
	
	private static void handleHit(Actor subject, Actor target, Limb limb, ItemWeapon weapon) {
		int damage = weapon.getDamage();
		boolean crit = false;
		if(ThreadLocalRandom.current().nextFloat() < ItemWeapon.CRIT_CHANCE) {
			damage += weapon.getCritDamage();
			crit = true;
		}
		List<Action> reactions = weapon.reactionActions(target);
		Context attackContext = new Context(subject, false, target, false, weapon, false);
		Context reactionContext = new Context(target, false, subject, false, weapon, false);
		String hitPhrase;
		if(limb == null) {
			hitPhrase = weapon.isRanged() ? (crit ? "rangedHitCrit" : "rangedHit") : (crit ? "meleeHitCrit" : "meleeHit");
		} else {
			hitPhrase = weapon.isRanged() ? (crit ? limb.getRangedCritHitPhrase() : limb.getRangedHitPhrase()) : (crit ? limb.getMeleeCritHitPhrase() : limb.getMeleeHitPhrase());
		}
		if(reactions.isEmpty()) {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(hitPhrase), attackContext, null, null));
			if(limb == null) {
				target.damage(damage);
			} else {
				target.damageLimb(damage, limb);
			}
		} else {
			ActionReaction reaction = (ActionReaction) target.chooseAction(weapon.reactionActions(target));
			switch(reaction.getType()) {
			case BLOCK:
				if(ThreadLocalRandom.current().nextFloat() < BLOCK_CHANCE) {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("blockSuccess"), reactionContext, null, null));
				} else {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("blockFail"), reactionContext, null, null));
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(hitPhrase), attackContext, null, null));
					if(limb == null) {
						target.damage(damage);
					} else {
						target.damageLimb(damage, limb);
					}
				}
				break;
			case DODGE:
				if(ThreadLocalRandom.current().nextFloat() < DODGE_CHANCE) {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("dodgeSuccess"), reactionContext, null, null));
				} else {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("dodgeFail"), reactionContext, null, null));
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(hitPhrase), attackContext, null, null));
					if(limb == null) {
						target.damage(damage);
					} else {
						target.damageLimb(damage, limb);
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	public static float calculateHitChance(Actor attacker, Actor target, Limb limb, ItemWeapon weapon) {
		float skill = (float) attacker.getSkill(weapon.getSkill());
		float chance = HIT_CHANCE_MIN + ((HIT_CHANCE_MAX - HIT_CHANCE_MIN) / 9.0f) * (skill - 1.0f);
		if(limb != null) {
			chance *= limb.getHitChance();
		}
		if(weapon.isRanged()) {
			int distance = Pathfinder.findPath(attacker.getArea(), target.getArea()).size() - 1;
			float rangePenalty = Math.min(distance * RANGE_PENALTY, RANGE_PENALTY_MAX);
			chance *= (1.0f - rangePenalty);
		}
		return chance;
	}

}
