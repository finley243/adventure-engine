package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemConsume;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateConsumable;

import java.util.List;

public class ItemComponentConsumable extends ItemComponent {

    public ItemComponentConsumable(Item item, ItemComponentTemplateConsumable template) {
        super(item, template);
    }

    private ItemComponentTemplateConsumable getConsumableTemplate() {
        return (ItemComponentTemplateConsumable) getTemplate();
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        actions.add(new ActionItemConsume(getItem(), getConsumableTemplate().getConsumePrompt(), getConsumableTemplate().getConsumePhrase(), getConsumableTemplate().getEffects()));
        return actions;
    }

}
