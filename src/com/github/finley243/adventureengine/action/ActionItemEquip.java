package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemEquippable;

import java.util.Set;

public class ActionItemEquip extends Action {

    public static final float SUBOPTIMAL_WEAPON_UTILITY = 0.4f;
    public static final float OPTIMAL_WEAPON_UTILITY = 0.7f;

    private final ItemEquippable item;
    private final Set<String> slots;

    public ActionItemEquip(ItemEquippable item, Set<String> slots) {
        this.item = item;
        this.slots = slots;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getEquipmentComponent().equip(item, slots);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("equip"), context, this, null, subject, null));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && subject.getEquipmentComponent().isSlotEmpty(slots) && item.getEquippedActor() == null;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        StringBuilder slotLabel = new StringBuilder();
        boolean first = true;
        for (String slot : slots) {
            if (first) {
                first = false;
            } else {
                slotLabel.append(", ");
            }
            slotLabel.append(LangUtils.titleCase(subject.getEquipSlots().get(slot).name()));
        }
        return new MenuChoice("Equip (" + slotLabel + ")", canChoose(subject), new String[]{"Inventory", Inventory.getItemNameFormatted(item, subject.getInventory())}, new String[]{"equip " + item.getName(), "put on " + item.getName()});
    }

    @Override
    public float utility(Actor subject) {
        if (item instanceof ItemWeapon weapon) {
            if (!subject.isInCombat()) return 0;
            if (weapon.isRanged()) {
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
