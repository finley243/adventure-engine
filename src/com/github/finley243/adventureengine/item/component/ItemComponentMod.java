package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionModInstall;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateMod;

import java.util.List;

public class ItemComponentMod extends ItemComponent {

    public ItemComponentMod(Item item, ItemComponentTemplateMod template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private ItemComponentTemplateMod getModTemplate() {
        return (ItemComponentTemplateMod) getTemplate();
    }

    public List<String> getEffects() {
        return getModTemplate().getEffects();
    }

    public String getModSlot() {
        return getModTemplate().getModSlot();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(subject);
        for (Item item : subject.getInventory().getItems()) {
            if (item.hasComponentOfType(ItemComponentModdable.class)) {
                actions.add(new ActionModInstall(item, getItem()));
            }
        }
        return actions;
    }

}
