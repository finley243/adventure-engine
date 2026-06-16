package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.EquippableItemComponentTemplate;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EquippableItemComponent extends ItemComponent {

    private Actor equippedActor;
    private EquippableItemComponentTemplate.EquippableSlotsData equippedSlotsData;

    public EquippableItemComponent(Item item, EquippableItemComponentTemplate template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private EquippableItemComponentTemplate getEquippableTemplate() {
        return (EquippableItemComponentTemplate) getTemplate();
    }

    @Override
    protected List<Action> getPossibleInventoryActions(ScriptRuntime scriptRuntime, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(scriptRuntime, subject);
        for (EquippableItemComponentTemplate.EquippableSlotsData slots : getEquippableTemplate().getSlots()) {
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

    private void setEquippedSlots(EquippableItemComponentTemplate.EquippableSlotsData slots) {
        this.equippedSlotsData = slots;
    }

    private List<Effect> getEquippedEffects() {
        return equippedSlotsData.equippedEffects();
    }

    public boolean isValidEquipData(EquippableItemComponentTemplate.EquippableSlotsData slotsData) {
        return getEquippableTemplate().getSlots().contains(slotsData);
    }

    public void onEquip(Actor target, EquippableItemComponentTemplate.EquippableSlotsData slots) {
        setEquippedActor(target);
        setEquippedSlots(slots);
        for (Effect effect : getEquippedEffects()) {
            target.getEffectComponent().addEffect(effect);
        }
    }

    public void onUnequip(Actor target) {
        for (Effect effect : getEquippedEffects()) {
            target.getEffectComponent().removeEffect(effect);
        }
        setEquippedActor(null);
        setEquippedSlots(null);
    }

    public List<Action> equippedActions(Actor subject, ScriptRuntime scriptRuntime) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionItemUnequip(getItem()));
        for (String exposedComponent : equippedSlotsData.componentsExposed()) {
            actions.addAll(getItem().getComponentOfType(ItemComponentFactory.getClassFromName(exposedComponent)).getPossibleInventoryActions(scriptRuntime, subject));
        }
        for (ActionCustom.CustomActionHolder equippedAction : equippedSlotsData.equippedActions()) {
            ActionTemplate customActionTemplate = equippedAction.action();
            actions.add(new ActionCustom(scriptRuntime, null, null, getItem(), null, customActionTemplate, equippedAction.parameters(), new MenuDataInventory(getItem(), subject.getInventory()), false));
        }
        return actions;
    }

}
