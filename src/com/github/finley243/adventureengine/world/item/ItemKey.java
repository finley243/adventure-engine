package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.item.stats.StatsKey;

import java.util.HashSet;
import java.util.Set;

public class ItemKey extends Item {

	private final StatsKey stats;
	
	public ItemKey(Game game, String ID, boolean isGenerated, StatsKey stats) {
		super(game, isGenerated, ID, stats.getName(), stats.getDescription(), stats.getScripts());
		this.stats = stats;
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("key");
		return tags;
	}
	
	@Override
	public String getStatsID() {
		return stats.getID();
	}

}
