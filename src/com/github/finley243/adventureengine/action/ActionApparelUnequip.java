package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemApparel;

public class ActionApparelUnequip extends Action {

    private final ItemApparel item;

    public ActionApparelUnequip(ItemApparel item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getApparelComponent().unequip(item);
        Context context = new Context(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("unequip"), context, this, null, subject, null));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Unequip", canChoose(subject), new String[]{"inventory", item.getName()}, new String[]{"unequip " + item.getName(), "take off " + item.getName()});
    }

}
