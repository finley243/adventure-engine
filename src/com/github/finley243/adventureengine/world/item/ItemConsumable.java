package com.github.finley243.adventureengine.world.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemConsume;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.stats.StatsConsumable;
import com.github.finley243.adventureengine.world.item.stats.StatsConsumable.ConsumableType;

public class ItemConsumable extends Item {

	private final StatsConsumable stats;
	
	public ItemConsumable(Game game, String ID, Area area, boolean isGenerated, StatsConsumable stats) {
		super(game, isGenerated, ID, area, stats.getName(), stats.getDescription(), stats.getScripts());
		this.stats = stats;
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("consumable");
		return tags;
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
