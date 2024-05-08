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
    private Set<String> equippedSlots;

    public ItemComponentEquippable(Item item, ItemComponentTemplateEquippable template) {
        super(item, template);
    }

    private ItemComponentTemplateEquippable getEquippableTemplate() {
        return (ItemComponentTemplateEquippable) getTemplate();
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        for (Set<String> slots : getEquipSlots()) {
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
        return equippedSlots;
    }

    private void setEquippedSlots(Set<String> slots) {
        this.equippedSlots = slots;
    }

    public Set<Set<String>> getEquipSlots() {
        return getEquippableTemplate().getSlots();
    }

    public List<String> getEquippedEffects() {
        return getEquippableTemplate().getEquippedEffects();
    }

    public void onEquip(Actor target, Set<String> slots) {
        for (String effect : getEquippedEffects()) {
            target.getEffectComponent().addEffect(effect);
        }
        setEquippedActor(target);
        setEquippedSlots(slots);
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
        for (ActionCustom.CustomActionHolder equippedAction : getEquippableTemplate().getEquippedActions()) {
            actions.add(new ActionCustom(getItem().game(), null, null, getItem(), null, equippedAction.action(), equippedAction.parameters(), new MenuDataInventory(getItem(), subject.getInventory()), false));
        }
        return actions;
    }

}
