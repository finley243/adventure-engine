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

	private final String templateID;
	
	public ItemConsumable(Game game, String ID, String templateID) {
		super(game, ID);
		this.templateID = templateID;
	}

	@Override
	public ItemTemplate getTemplate() {
		return getConsumableTemplate();
	}

	public ConsumableTemplate getConsumableTemplate() {
		return (ConsumableTemplate) game().data().getItem(templateID);
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
