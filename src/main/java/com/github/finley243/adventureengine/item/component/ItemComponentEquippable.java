package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateEquippable;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.script.ScriptRuntime;

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
    protected List<Action> getPossibleInventoryActions(ScriptRuntime scriptRuntime, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(scriptRuntime, subject);
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

    public void onEquip(Game game, Actor target, ItemComponentTemplateEquippable.EquippableSlotsData slots) {
        setEquippedActor(target);
        setEquippedSlots(slots);
        for (String effectID : getEquippedEffects()) {
            Effect effect = game.data().getEffect(effectID);
            target.getEffectComponent().addEffect(game, effect);
        }
    }

    public void onUnequip(Game game, Actor target) {
        for (String effectID : getEquippedEffects()) {
            Effect effect = game.data().getEffect(effectID);
            target.getEffectComponent().removeEffect(game, effect);
        }
        setEquippedActor(null);
        setEquippedSlots(null);
    }

    public List<Action> equippedActions(Game game, Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionItemUnequip(getItem()));
        for (String exposedComponent : equippedSlotsData.componentsExposed()) {
            actions.addAll(getItem().getComponentOfType(ItemComponentFactory.getClassFromName(exposedComponent)).getPossibleInventoryActions(game, subject));
        }
        for (ActionCustom.CustomActionHolder equippedAction : equippedSlotsData.equippedActions()) {
            ActionTemplate customActionTemplate = game.data().getActionTemplate(equippedAction.action());
            actions.add(new ActionCustom(null, null, getItem(), null, customActionTemplate, equippedAction.parameters(), new MenuDataInventory(getItem(), subject.getInventory()), false));
        }
        return actions;
    }

}
