package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionItemThrow extends Action {

    private final Area area;
    private final Item item;

    public ActionItemThrow(Area area, Item item) {
        this.area = area;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.inventory().removeItem(item);
        area.addObject(item);
        subject.game().eventBus().post(new SoundEvent(area, false));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(LangUtils.titleCase(area.getName()), canChoose(subject), new String[]{"inventory", item.getName() + subject.inventory().itemCountLabel(item.getStatsID()), "throw"});
    }
}
