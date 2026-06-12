package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionModRemove;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateModdable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemComponentModdable extends ItemComponent {

    private final Map<String, List<Item>> mods;

    public ItemComponentModdable(Item item, ItemComponentTemplate template) {
        super(item, template);
        this.mods = new HashMap<>();
    }

    @Override
    public boolean hasState() {
        return true;
    }

    private ItemComponentTemplateModdable getModdableTemplate() {
        return (ItemComponentTemplateModdable) getTemplate();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(Game game, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(game, subject);
        for (List<Item> modList : mods.values()) {
            for (Item mod : modList) {
                actions.add(new ActionModRemove(getItem(), mod));
            }
        }
        return actions;
    }

    public boolean canInstallMod(Item mod) {
        String modSlot = mod.getComponentOfType(ItemComponentMod.class).getModSlot();
        return getModdableTemplate().getModSlots().containsKey(modSlot) && (!mods.containsKey(modSlot) || mods.get(modSlot).size() < getModdableTemplate().getModSlots().get(modSlot));
    }

    public void installMod(Item mod) {
        for (Effect effect : mod.getComponentOfType(ItemComponentMod.class).getEffects()) {
            getItem().getComponentOfType(ItemComponentEffectible.class).addEffect(effect);
        }
        String modSlot = mod.getComponentOfType(ItemComponentMod.class).getModSlot();
        if (!mods.containsKey(modSlot)) {
            mods.put(modSlot, new ArrayList<>());
        }
        mods.get(modSlot).add(mod);
    }

    public void removeMod(Item mod) {
        for (Effect effect : mod.getComponentOfType(ItemComponentMod.class).getEffects()) {
            getItem().getComponentOfType(ItemComponentEffectible.class).removeEffect(effect);
        }
        String modSlot = mod.getComponentOfType(ItemComponentMod.class).getModSlot();
        mods.get(modSlot).remove(mod);
        if (mods.get(modSlot).isEmpty()) {
            mods.remove(modSlot);
        }
    }

}
