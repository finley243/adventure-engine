package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.menu.data.MenuDataNested;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionThrow extends Action {

    private final Area area;
    private final Item item;

    public ActionThrow(Area area, Item item) {
        this.area = area;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.inventory().removeItem(item);
        area.addObject(item);
        Game.EVENT_BUS.post(new SoundEvent(area, false));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataNested(LangUtils.titleCase(area.getName()), "Throw " + item.getFormattedName(false) + " towards " + area.getFormattedName(false), canChoose(subject), new String[]{"inventory", item.getName(), "throw"});
    }
}
