package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemEquip;
import com.github.finley243.adventureengine.action.ActionItemUnequip;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemEquippable extends Item {

	private Actor equippedActor;

	public ItemEquippable(Game game, String ID, String templateID) {
		super(game, ID, templateID);
	}

	public Actor getEquippedActor() {
		return equippedActor;
	}

	public void setEquippedActor(Actor actor) {
		this.equippedActor = actor;
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
