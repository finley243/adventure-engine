package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionItemUnequip extends Action {

    private final Item item;

    public ActionItemUnequip(Item item) {
        this.item = item;
    }

    @Override
    public String getID() {
        return "item_unequip";
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getEquipmentComponent().unequip(item);
        Context context = new Context(subject.game(), subject, null, item);
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("unequip"), context, true, this, null));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventory(item, subject.getInventory());
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Unequip";
    }

    @Override
    public float utility(Actor subject) {
        if (item.hasComponentOfType(ItemComponentWeapon.class) && !subject.isInCombat()) {
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
