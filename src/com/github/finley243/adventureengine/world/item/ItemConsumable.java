package com.github.finley243.adventureengine.world.item;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionConsume;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.template.StatsConsumable;
import com.github.finley243.adventureengine.world.template.StatsConsumable.ConsumableType;

public class ItemConsumable extends Item {

	private StatsConsumable stats;
	
	public ItemConsumable(StatsConsumable stats) {
		super(stats.getName());
		this.stats = stats;
	}
	
	@Override
	public int getPrice() {
		return stats.getPrice();
	}
	
	public ConsumableType getConsumableType() {
		return stats.getType();
	}
	
	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionConsume(this));
		return actions;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemConsumable)) {
			return false;
		} else {
			ItemConsumable other = (ItemConsumable) o;
			return this.stats == other.stats;
		}
	}
	
}
