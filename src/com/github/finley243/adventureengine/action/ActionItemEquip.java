package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentEquippable;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateEquippable;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionItemEquip extends Action {

    public static final float SUBOPTIMAL_WEAPON_UTILITY = 0.4f;
    public static final float OPTIMAL_WEAPON_UTILITY = 0.7f;

    private final Item item;
    private final ItemComponentTemplateEquippable.EquippableSlotsData slotsData;

    public ActionItemEquip(Item item, ItemComponentTemplateEquippable.EquippableSlotsData slotsData) {
        this.item = item;
        this.slotsData = slotsData;
    }

    @Override
    public String getID() {
        return "item_equip";
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getEquipmentComponent().equip(item, slotsData);
        Context context = new Context(subject.game(), subject, null, item);
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("equip"), context, true, this, null));
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (item.getComponentOfType(ItemComponentEquippable.class).getEquippedActor() != null) {
            return new CanChooseResult(false, "Already equipped");
        }
        if (subject.getEquipmentComponent().isSlotBlocked(slotsData.slots())) {
            return new CanChooseResult(false, "Equipping this item is blocked");
        }
        if (!subject.getEquipmentComponent().isSlotEmpty(slotsData.slots())) {
            return new CanChooseResult(false, "Another item is already equipped");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventory(item, subject.getInventory());
    }

    @Override
    public String getPrompt(Actor subject) {
        StringBuilder slotLabel = new StringBuilder();
        boolean first = true;
        for (String slot : slotsData.slots()) {
            if (first) {
                first = false;
            } else {
                slotLabel.append(", ");
            }
            slotLabel.append(LangUtils.titleCase(subject.getEquipSlots().get(slot).name()));
        }
        return "Equip (" + slotLabel + ")";
    }

    @Override
    public float utility(Actor subject) {
        if (item.hasComponentOfType(ItemComponentWeapon.class)) {
            if (!subject.isInCombat()) return 0;
            if (item.getComponentOfType(ItemComponentWeapon.class).isRanged()) {
                if (UtilityUtils.actorHasMeleeTargets(subject)) {
                    return SUBOPTIMAL_WEAPON_UTILITY;
                } else {
                    return OPTIMAL_WEAPON_UTILITY;
                }
            } else {
                if (UtilityUtils.actorHasMeleeTargets(subject)) {
                    return OPTIMAL_WEAPON_UTILITY;
                } else {
                    return SUBOPTIMAL_WEAPON_UTILITY;
                }
            }
        } else {
            return 0.0f;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemEquip other)) {
            return false;
        } else {
            return other.item == this.item;
        }
    }

}
