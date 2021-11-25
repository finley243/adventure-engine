package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionThrow implements Action {

    private boolean disabled;
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
    public boolean canChoose(Actor subject) {
        return !disabled;
    }

    @Override
    public void disable() {
        disabled = true;
    }

    @Override
    public String getPrompt() {
        return "Throw " + item.getFormattedName(false) + " towards " + area.getFormattedName(false);
    }

    @Override
    public float utility(Actor subject) {
        return 0;
    }

    @Override
    public boolean usesAction() {
        return true;
    }

    @Override
    public boolean canRepeat() {
        return false;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return false;
    }

    @Override
    public int actionCount() {
        return 1;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventory("Throw towards " + area.getName(), canChoose(subject), item);
    }
}
