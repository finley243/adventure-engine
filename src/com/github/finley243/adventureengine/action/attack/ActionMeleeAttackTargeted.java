package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.reaction.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ActionMeleeAttackTargeted extends ActionAttack {


	public ActionMeleeAttackTargeted(ItemWeapon weapon, Actor target, Limb limb) {
		super(weapon, target, limb);
	}

	@Override
	public List<ActionReaction> getReactions(Actor subject) {
		List<ActionReaction> reactions = new ArrayList<>();
		reactions.add(new ActionReactionBlock(subject, getWeapon()));
		reactions.add(new ActionReactionDodge(subject, getWeapon()));
		reactions.add(new ActionReactionCounter(subject, getWeapon()));
		reactions.add(new ActionReactionNone(subject, getWeapon()));
		return reactions;
	}

	@Override
	public String getTelegraphPhrase() {
		return "meleeTelegraph";
	}

	@Override
	public String getHitPhrase() {
		return "meleeHitLimb";
	}

	@Override
	public String getMissPhrase() {
		return "meleeMissLimb";
	}

	@Override
	public int repeatCount(Actor subject) {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(getLimb().getName()) + " (" + getChanceTag(subject) + ")",
				canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName(), "Targeted Attack"});
	}

}
