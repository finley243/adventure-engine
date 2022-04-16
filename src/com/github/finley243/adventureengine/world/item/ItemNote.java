package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionReadNote;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;
import com.github.finley243.adventureengine.world.item.template.NoteTemplate;

import java.util.List;

public class ItemNote extends Item {

    private final NoteTemplate stats;

    public ItemNote(Game game, String ID, Area area, boolean isGenerated, NoteTemplate stats) {
        super(game, isGenerated, ID, area, stats.getName(), stats.getDescription(), stats.getScripts());
        this.stats = stats;
    }

    public List<String> getText() {
        return stats.getText();
    }

    @Override
    public ItemTemplate getTemplate() {
        return stats;
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        actions.add(new ActionReadNote(this));
        return actions;
    }

}
