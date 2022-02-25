package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.template.StatsKey;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemKey extends Item {

	private final StatsKey stats;
	
	public ItemKey(StatsKey stats) {
		super(stats.generateInstanceID(), stats.getName(), stats.getDescription(), stats.getScripts());
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
