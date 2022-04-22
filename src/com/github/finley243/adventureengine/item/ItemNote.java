package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionReadNote;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.NoteTemplate;

import java.util.List;

public class ItemNote extends Item {

    private final NoteTemplate stats;

    public ItemNote(Game game, String ID, NoteTemplate stats) {
        super(game, ID);
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
