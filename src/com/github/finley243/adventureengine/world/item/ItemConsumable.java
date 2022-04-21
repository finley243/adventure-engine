package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemConsume;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.world.item.template.ConsumableTemplate;
import com.github.finley243.adventureengine.world.item.template.ConsumableTemplate.ConsumableType;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;

import java.util.List;

public class ItemConsumable extends Item {

	private final ConsumableTemplate stats;
	
	public ItemConsumable(Game game, String ID, ConsumableTemplate stats) {
		super(game, ID);
		this.stats = stats;
	}

	@Override
	public ItemTemplate getTemplate() {
		return stats;
	}
	
	public ConsumableType getConsumableType() {
		return stats.getType();
	}
	
	public List<Effect> getEffects() {
		return stats.getEffects();
	}
	
	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		actions.add(new ActionItemConsume(this));
		return actions;
	}
	
}
