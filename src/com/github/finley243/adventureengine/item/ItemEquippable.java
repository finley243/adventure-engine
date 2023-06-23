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
	private Set<String> equippedSlots;

	public ItemEquippable(Game game, String ID, String templateID) {
		super(game, ID, templateID);
	}

	private EquippableTemplate getApparelTemplate() {
		return (EquippableTemplate) getTemplate();
	}

	public Actor getEquippedActor() {
		return equippedActor;
	}

	private void setEquippedActor(Actor actor) {
		this.equippedActor = actor;
	}

	public Set<String> getEquippedSlots() {
		return equippedSlots;
	}

	private void setEquippedSlots(Set<String> slots) {
		this.equippedSlots = slots;
	}

	public Set<Set<String>> getEquipSlots() {
		return getApparelTemplate().getSlots();
	}

	public List<String> getEquippedEffects() {
		return getApparelTemplate().getEquippedEffects();
	}

	public void onEquip(Actor target, Set<String> slots) {
		for (String effect : getEquippedEffects()) {
			target.getEffectComponent().addEffect(effect);
		}
		setEquippedActor(target);
		setEquippedSlots(slots);
	}

	public void onUnequip(Actor target) {
		for (String effect : getEquippedEffects()) {
			target.getEffectComponent().removeEffect(effect);
		}
		setEquippedActor(null);
		setEquippedSlots(null);
	}

	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		for (Set<String> slots : getEquipSlots()) {
			actions.add(new ActionItemEquip(this, slots));
		}
		return actions;
	}

	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemUnequip(this));
		return actions;
	}

}
