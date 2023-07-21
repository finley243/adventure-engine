package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionItemEquip;
import com.github.finley243.adventureengine.action.ActionItemUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantBoolean;
import com.github.finley243.adventureengine.item.template.EquippableTemplate;
import com.github.finley243.adventureengine.stat.StatHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemEquippable extends Item {

	private Actor equippedActor;
	private Set<String> equippedSlots;

	public ItemEquippable(Game game, String ID, String templateID) {
		super(game, ID, templateID);
	}

	private EquippableTemplate getEquippableTemplate() {
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
		return getEquippableTemplate().getSlots();
	}

	public List<String> getEquippedEffects() {
		return getEquippableTemplate().getEquippedEffects();
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
		for (ActionCustom.CustomActionHolder equippedAction : getEquippableTemplate().getEquippedActions()) {
			actions.add(new ActionCustom(game(), null, null, this, null, equippedAction.action(), equippedAction.parameters(), new String[] {Inventory.getItemNameFormatted(this, subject.getInventory())}, false));
		}
		return actions;
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "has_equipped_actor" -> new ExpressionConstantBoolean(equippedActor != null);
			default -> super.getStatValue(name, context);
		};
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		if ("equipped_actor".equals(name)) {
			return equippedActor;
		}
		return super.getSubHolder(name, ID);
	}

}
