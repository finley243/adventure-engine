package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemApparel;

public class ActionApparelUnequip extends Action {

    private final ItemApparel item;

    public ActionApparelUnequip(ItemApparel item) {
        super(ActionDetectionChance.LOW);
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.apparelComponent().unequip(item);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", item).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("unequip"), context, this, subject));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Unequip", canChoose(subject), new String[]{"inventory", item.getName()});
    }
}
