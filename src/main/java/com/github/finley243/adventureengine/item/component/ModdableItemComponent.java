package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.action.ActionModRemove;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ModdableItemComponentTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModdableItemComponent extends ItemComponent {

    private final Map<String, List<Item>> mods;

    public ModdableItemComponent(Item item, ItemComponentTemplate template) {
        super(item, template);
        this.mods = new HashMap<>();
    }

    @Override
    public boolean hasState() {
        return true;
    }

    private ModdableItemComponentTemplate getModdableTemplate() {
        return (ModdableItemComponentTemplate) getTemplate();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(ActionDependencies dependencies, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(dependencies, subject);
        for (List<Item> modList : mods.values()) {
            for (Item mod : modList) {
                actions.add(new ActionModRemove(dependencies, getItem(), mod));
            }
        }
        return actions;
    }

    public boolean canInstallMod(Item mod) {
        String modSlot = mod.getComponentOfType(ModItemComponent.class).getModSlot();
        return getModdableTemplate().getModSlots().containsKey(modSlot) && (!mods.containsKey(modSlot) || mods.get(modSlot).size() < getModdableTemplate().getModSlots().get(modSlot));
    }

    public void installMod(Item mod) {
        for (Effect effect : mod.getComponentOfType(ModItemComponent.class).getEffects()) {
            getItem().getComponentOfType(EffectableItemComponent.class).addEffect(effect);
        }
        String modSlot = mod.getComponentOfType(ModItemComponent.class).getModSlot();
        if (!mods.containsKey(modSlot)) {
            mods.put(modSlot, new ArrayList<>());
        }
        mods.get(modSlot).add(mod);
    }

    public void removeMod(Item mod) {
        for (Effect effect : mod.getComponentOfType(ModItemComponent.class).getEffects()) {
            getItem().getComponentOfType(EffectableItemComponent.class).removeEffect(effect);
        }
        String modSlot = mod.getComponentOfType(ModItemComponent.class).getModSlot();
        mods.get(modSlot).remove(mod);
        if (mods.get(modSlot).isEmpty()) {
            mods.remove(modSlot);
        }
    }

}
