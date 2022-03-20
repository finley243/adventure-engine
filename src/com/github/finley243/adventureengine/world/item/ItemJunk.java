package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.item.stats.StatsJunk;

import java.util.HashSet;
import java.util.Set;

public class ItemJunk extends Item {

    private final StatsJunk stats;

    public ItemJunk(Game game, String ID, boolean isGenerated, StatsJunk stats) {
        super(game, isGenerated, ID, stats.getName(), stats.getDescription(), stats.getScripts());
        this.stats = stats;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("junk");
        return tags;
    }

    @Override
    public String getStatsID() {
        return stats.getID();
    }

}
