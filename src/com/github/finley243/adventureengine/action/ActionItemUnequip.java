package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;

public class ActionItemUnequip extends Action {

    private final ItemEquippable item;

    public ActionItemUnequip(ItemEquippable item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getEquipmentComponent().unequip(item);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
        (new SensoryEvent(subject.getArea(), Phrases.get("unequip"), context, true, this, null, subject, null)).execute(subject.game());
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
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
