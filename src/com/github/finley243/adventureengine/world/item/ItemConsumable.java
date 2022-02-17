package com.github.finley243.adventureengine.world.item;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemConsume;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.template.StatsConsumable;
import com.github.finley243.adventureengine.world.template.StatsConsumable.ConsumableType;

public class ItemConsumable extends Item {

	private final StatsConsumable stats;
	
	public ItemConsumable(StatsConsumable stats) {
		super(stats.generateInstanceID(), stats.getName(), stats.getDescription(), stats.getScripts());
		this.stats = stats;
	}
	
	@Override
	public int getPrice() {
		return stats.getPrice();
	}
	
	@Override
	public String getStatsID() {
		return stats.getID();
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
		actions.add(0, new ActionItemConsume(this));
		return actions;
	}
	
}
