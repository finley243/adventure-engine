package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemApparel;

public class ActionApparelEquip extends Action {

    private final ItemApparel item;

    public ActionApparelEquip(ItemApparel item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.apparelComponent().equip(item);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", item).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("equip"), context, this, subject));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return !disabled && subject.apparelComponent().isSlotEmpty(item);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Equip", canChoose(subject), new String[]{"inventory", item.getName()});
    }
}
