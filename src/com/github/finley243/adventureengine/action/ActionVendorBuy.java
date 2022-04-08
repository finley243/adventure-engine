package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionVendorBuy extends Action {

    private final Actor vendor;
    private final Inventory vendorInventory;
    private final Item item;

    public ActionVendorBuy(Actor vendor, Inventory vendorInventory, Item item) {
        this.vendor = vendor;
        this.vendorInventory = vendorInventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        vendorInventory.removeItem(item);
        subject.adjustMoney(-item.getPrice());
        subject.inventory().addItem(item);
        Context context = new Context(subject, item, vendor);
        subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("buy"), context, this, subject));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return !disabled && subject.getMoney() >= item.getPrice();
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(LangUtils.titleCase(item.getName()) + " [" + item.getPrice() + "]", canChoose(subject), new String[]{vendor.getName(), "buy"});
    }

}
