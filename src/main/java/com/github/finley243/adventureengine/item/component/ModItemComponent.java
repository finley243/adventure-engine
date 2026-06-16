package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.action.ActionModInstall;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ModItemComponentTemplate;

import java.util.List;

public class ModItemComponent extends ItemComponent {

    public ModItemComponent(Item item, ModItemComponentTemplate template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private ModItemComponentTemplate getModTemplate() {
        return (ModItemComponentTemplate) getTemplate();
    }

    public List<Effect> getEffects() {
        return getModTemplate().getEffects();
    }

    public String getModSlot() {
        return getModTemplate().getModSlot();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(ActionDependencies dependencies, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(dependencies, subject);
        for (Item item : subject.getInventory().getItems()) {
            if (item.hasComponentOfType(ModdableItemComponent.class)) {
                actions.add(new ActionModInstall(subject, dependencies, item, getItem()));
            }
        }
        return actions;
    }

}
