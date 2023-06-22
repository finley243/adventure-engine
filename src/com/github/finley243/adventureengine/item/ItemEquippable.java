package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemEquip;
import com.github.finley243.adventureengine.action.ActionItemUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.template.EquippableTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemEquippable extends Item {

	private Actor equippedActor;

	public ItemEquippable(Game game, String ID, String templateID) {
		super(game, ID, templateID);
	}

	private EquippableTemplate getApparelTemplate() {
		return (EquippableTemplate) getTemplate();
	}

	public Actor getEquippedActor() {
		return equippedActor;
	}

	public void setEquippedActor(Actor actor) {
		this.equippedActor = actor;
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
		actions.add(new ActionItemEquip(this));
		return actions;
	}

	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemUnequip(this));
		return actions;
	}

}
