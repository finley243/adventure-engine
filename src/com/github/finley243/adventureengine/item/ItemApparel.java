package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionApparelEquip;
import com.github.finley243.adventureengine.action.ActionApparelUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.template.ApparelTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemApparel extends Item {

	public ItemApparel(Game game, String ID, String templateID) {
		super(game, ID, templateID);
	}

	private ApparelTemplate getApparelTemplate() {
		return (ApparelTemplate) getTemplate();
	}

	public Set<String> getEquipSlots() {
		return getApparelTemplate().getSlots();
	}

	public List<String> getEquippedEffects() {
		return getApparelTemplate().getEquippedEffects();
	}

	public void onEquip(Actor target) {
		for (String effect : getEquippedEffects()) {
			target.getEffectComponent().addEffect(effect);
		}
	}

	public void onUnequip(Actor target) {
		for (String effect : getEquippedEffects()) {
			target.getEffectComponent().removeEffect(effect);
		}
	}

	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		actions.add(new ActionApparelEquip(this));
		return actions;
	}

	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionApparelUnequip(this));
		return actions;
	}

}
