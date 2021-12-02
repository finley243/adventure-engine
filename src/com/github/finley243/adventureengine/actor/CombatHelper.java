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

	public static final float AUTOFIRE_DAMAGE_MULT = 4.00f;
	public static final float AUTOFIRE_HIT_CHANCE_MULT = 0.80f;

	public static final float BLOCK_CHANCE = 0.30f;
	public static final float DODGE_CHANCE = 0.40f;

	public static Context lastAttack;

	public static void newTurn() {
		lastAttack = null;
	}

	public static void handleAttack(Actor subject, Actor target, ItemWeapon weapon, Limb limb, boolean auto) {
		boolean isRepeat = lastAttack != null && lastAttack.getSubject() == subject && lastAttack.getObject() == target && lastAttack.getObject2() == weapon;
		target.addCombatTarget(subject);
		Context attackContext = new Context(subject, false, target, false, weapon, false);
		if(!isRepeat) {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(getTelegraphPhrase(weapon, limb, auto)), attackContext, null, null));
		}
		if(ThreadLocalRandom.current().nextFloat() < calculateHitChance(subject, target, limb, weapon, auto)) {
			handleHit(subject, target, limb, weapon, auto);
		} else {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(getMissPhrase(weapon, limb, auto)), attackContext, null, null));
		}
		lastAttack = attackContext;
	}
	
	private static void handleHit(Actor subject, Actor target, Limb limb, ItemWeapon weapon, boolean auto) {
		int damage = weapon.getDamage();
		if(auto) {
			damage *= AUTOFIRE_DAMAGE_MULT;
		}
		boolean crit = false;
		if(ThreadLocalRandom.current().nextFloat() < ItemWeapon.CRIT_CHANCE) {
			damage += weapon.getCritDamage();
			crit = true;
		}
		List<Action> reactions = weapon.reactionActions(target);
		Context attackContext = new Context(subject, false, target, false, weapon, false);
		Context reactionContext = new Context(target, false, subject, false, weapon, false);
		String hitPhrase = getHitPhrase(weapon, limb, crit, auto);
		if(reactions.isEmpty()) {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(hitPhrase), attackContext, null, null));
			if(limb == null) {
				target.damage(damage);
			} else {
				target.damageLimb(damage, limb);
			}
		} else {
			ActionReaction reaction = (ActionReaction) target.chooseAction(reactions);
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
	
	public static float calculateHitChance(Actor attacker, Actor target, Limb limb, ItemWeapon weapon, boolean auto) {
		float skill = (float) attacker.getSkill(weapon.getSkill());
		float chance = HIT_CHANCE_MIN + ((HIT_CHANCE_MAX - HIT_CHANCE_MIN) / (Actor.SKILL_MAX - Actor.SKILL_MIN)) * (skill - Actor.SKILL_MIN);
		if(limb != null) {
			chance *= limb.getHitChance();
		}
		if(auto) {
			chance *= AUTOFIRE_HIT_CHANCE_MULT;
		}
		if(weapon.isRanged()) {
			int distance = Pathfinder.findPath(attacker.getArea(), target.getArea()).size() - 1;
			float rangePenalty = Math.min(distance * RANGE_PENALTY, RANGE_PENALTY_MAX);
			chance *= (1.0f - rangePenalty);
		}
		return chance;
	}

	private static String getHitPhrase(ItemWeapon weapon, Limb limb, boolean crit, boolean auto) {
		if(weapon.isRanged()) {
			if(auto) {
				return crit ? "rangedAutoHitCrit" : "rangedAutoHit";
			} else if(limb != null) {
				return crit ? limb.getRangedCritHitPhrase() : limb.getRangedHitPhrase();
			} else {
				return crit ? "rangedHitCrit" : "rangedHit";
			}
		} else {
			if(limb != null) {
				return crit ? limb.getMeleeCritHitPhrase() : limb.getMeleeHitPhrase();
			} else {
				return crit ? "meleeHitCrit" : "meleeHit";
			}
		}
	}

	private static String getTelegraphPhrase(ItemWeapon weapon, Limb limb, boolean auto) {
		if(weapon.isRanged()) {
			return "rangedTelegraph";
		} else {
			return "meleeTelegraph";
		}
	}

	private static String getMissPhrase(ItemWeapon weapon, Limb limb, boolean auto) {
		if(weapon.isRanged()) {
			if(auto) {
				return "rangedAutoMiss";
			} else if(limb != null) {
				return limb.getRangedMissPhrase();
			} else {
				return "rangedMiss";
			}
		} else {
			if(limb != null) {
				return limb.getMeleeMissPhrase();
			} else {
				return "meleeMiss";
			}
		}
	}

}
