package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemConsume;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.template.ConsumableTemplate;
import com.github.finley243.adventureengine.item.template.ConsumableTemplate.ConsumableType;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

import java.util.List;

public class ItemConsumable extends Item {

	public ItemConsumable(Game game, String ID, String templateID) {
		super(game, ID, templateID);
	}

	private ConsumableTemplate getConsumableTemplate() {
		return (ConsumableTemplate) getTemplate();
	}
	
	public ConsumableType getConsumableType() {
		return getConsumableTemplate().getType();
	}
	
	public List<String> getEffects() {
		return getConsumableTemplate().getEffects();
	}
	
	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		actions.add(new ActionItemConsume(this));
		return actions;
	}
	
}
