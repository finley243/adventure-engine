package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.template.StatsJunk;

public class ItemJunk extends Item {

    private final StatsJunk stats;

    public ItemJunk(StatsJunk stats) {
        super(stats.generateInstanceID(), stats.getName(), stats.getDescription(), stats.getScripts());
        this.stats = stats;
    }

    @Override
    public String getStatsID() {
        return stats.getID();
    }

}
