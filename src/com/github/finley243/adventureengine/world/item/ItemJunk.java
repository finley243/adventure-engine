package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.JunkTemplate;

import java.util.HashSet;
import java.util.Set;

public class ItemJunk extends Item {

    private final JunkTemplate stats;

    public ItemJunk(Game game, String ID, Area area, boolean isGenerated, JunkTemplate stats) {
        super(game, isGenerated, ID, area, stats.getName(), stats.getDescription(), stats.getScripts());
        this.stats = stats;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("junk");
        return tags;
    }

    @Override
    public String getTemplateID() {
        return stats.getID();
    }

}
