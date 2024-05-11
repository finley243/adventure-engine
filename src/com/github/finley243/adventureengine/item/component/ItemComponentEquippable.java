package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionItemEquip;
import com.github.finley243.adventureengine.action.ActionItemUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateEquippable;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemComponentEquippable extends ItemComponent {

    private Actor equippedActor;
    private ItemComponentTemplateEquippable.EquippableSlotsData equippedSlotsData;

    public ItemComponentEquippable(Item item, ItemComponentTemplateEquippable template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private ItemComponentTemplateEquippable getEquippableTemplate() {
        return (ItemComponentTemplateEquippable) getTemplate();
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        for (ItemComponentTemplateEquippable.EquippableSlotsData slots : getEquippableTemplate().getSlots()) {
            actions.add(new ActionItemEquip(getItem(), slots));
        }
        return actions;
    }

    public Actor getEquippedActor() {
        return equippedActor;
    }

    private void setEquippedActor(Actor actor) {
        this.equippedActor = actor;
    }

    public Set<String> getEquippedSlots() {
        return equippedSlotsData.slots();
    }

    private void setEquippedSlots(ItemComponentTemplateEquippable.EquippableSlotsData slots) {
        this.equippedSlotsData = slots;
    }

    private List<String> getEquippedEffects() {
        return equippedSlotsData.equippedEffects();
    }

    public boolean isValidEquipData(ItemComponentTemplateEquippable.EquippableSlotsData slotsData) {
        return getEquippableTemplate().getSlots().contains(slotsData);
    }

    public void onEquip(Actor target, ItemComponentTemplateEquippable.EquippableSlotsData slots) {
        setEquippedActor(target);
        setEquippedSlots(slots);
        for (String effect : getEquippedEffects()) {
            target.getEffectComponent().addEffect(effect);
        }
    }

    public void onUnequip(Actor target) {
        for (String effect : getEquippedEffects()) {
            target.getEffectComponent().removeEffect(effect);
        }
        setEquippedActor(null);
        setEquippedSlots(null);
    }

    public List<Action> equippedActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionItemUnequip(getItem()));
        for (String exposedComponent : equippedSlotsData.componentsExposed()) {
            actions.addAll(getItem().getComponentOfType(ItemComponentFactory.getClassFromName(exposedComponent)).inventoryActions(subject));
        }
        for (ActionCustom.CustomActionHolder equippedAction : equippedSlotsData.equippedActions()) {
            actions.add(new ActionCustom(getItem().game(), null, null, getItem(), null, equippedAction.action(), equippedAction.parameters(), new MenuDataInventory(getItem(), subject.getInventory()), false));
        }
        return actions;
    }

}
