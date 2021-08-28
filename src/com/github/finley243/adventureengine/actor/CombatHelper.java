package com.github.finley243.adventureengine.actor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionReaction;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class CombatHelper {

	public static final float BLOCK_CHANCE = 0.3f;
	public static final float DODGE_CHANCE = 0.4f;
	
	public static void handleAttack(Actor subject, Actor target, ItemWeapon weapon) {
		target.addCombatTarget(subject);
		Context attackContext = new Context(subject, false, target, false, weapon, false);
		if(ThreadLocalRandom.current().nextFloat() < weapon.getHitChance(subject)) {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(weapon.isRanged() ? "rangedTelegraph" : "meleeTelegraph"), attackContext));
			handleReaction(subject, target, weapon);
		} else {
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(weapon.isRanged() ? "rangedMiss" : "meleeMiss"), attackContext));
		}
	}
	
	private static void handleReaction(Actor subject, Actor target, ItemWeapon weapon) {
		int damage = weapon.getDamage();
		if(ThreadLocalRandom.current().nextFloat() < ItemWeapon.CRIT_CHANCE) {
			damage += weapon.getCritDamage();
		}
		List<Action> reactions = weapon.reactionActions(target);
		Context reactionContext = new Context(target, false, subject, false, weapon, false);
		if(reactions.isEmpty()) {
			target.damage(damage);
		} else {
			ActionReaction reaction = (ActionReaction) target.chooseAction(weapon.reactionActions(target));
			switch(reaction.getType()) {
			case BLOCK:
				if(ThreadLocalRandom.current().nextFloat() < BLOCK_CHANCE) {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("blockSuccess"), reactionContext));
				} else {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("blockFail"), reactionContext));
					target.damage(damage);
				}
				break;
			case DODGE:
				if(ThreadLocalRandom.current().nextFloat() < DODGE_CHANCE) {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("dodgeSuccess"), reactionContext));
				} else {
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("dodgeFail"), reactionContext));
					target.damage(damage);
				}
				break;
			default:
				break;
			}
		}
	}
	
	

}
