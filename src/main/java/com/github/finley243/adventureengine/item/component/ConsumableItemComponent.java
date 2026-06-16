package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemConsume;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ConsumableItemComponentTemplate;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.List;

public class ConsumableItemComponent extends ItemComponent {

    public ConsumableItemComponent(Item item, ConsumableItemComponentTemplate template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private ConsumableItemComponentTemplate getConsumableTemplate() {
        return (ConsumableItemComponentTemplate) getTemplate();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(ScriptRuntime scriptRuntime, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(scriptRuntime, subject);
        actions.add(new ActionItemConsume(getItem(), getConsumableTemplate().getConsumePrompt(), getConsumableTemplate().getConsumePhrase(), getConsumableTemplate().getEffects()));
        return actions;
    }

}
