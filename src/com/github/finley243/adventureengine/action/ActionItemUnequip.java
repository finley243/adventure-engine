package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemEquippable;

public class ActionItemUnequip extends Action {

    private final ItemEquippable item;

    public ActionItemUnequip(ItemEquippable item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getEquipmentComponent().unequip(item);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("unequip"), context, this, null, subject, null));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Unequip", canChoose(subject), new String[]{"Inventory", Inventory.getItemNameFormatted(item, subject.getInventory())}, new String[]{"unequip " + item.getName(), "take off " + item.getName()});
    }

    @Override
    public float utility(Actor subject) {
        if (item instanceof ItemWeapon && !subject.isInCombat()) {
            return 0.4f;
        }
        return 0.0f;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemUnequip other)) {
            return false;
        } else {
            return other.item == this.item;
        }
    }

}
