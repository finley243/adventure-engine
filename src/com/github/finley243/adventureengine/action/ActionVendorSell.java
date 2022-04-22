package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.Item;

public class ActionVendorSell extends Action {

    private final Actor vendor;
    private final Inventory vendorInventory;
    private final Item item;

    public ActionVendorSell(Actor vendor, Inventory vendorInventory, Item item) {
        this.vendor = vendor;
        this.vendorInventory = vendorInventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.inventory().removeItem(item);
        subject.adjustMoney(item.getTemplate().getPrice());
        vendorInventory.addItem(item);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", item).put("vendor", vendor).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("sell"), context, this, subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(LangUtils.titleCase(item.getName()) + " [" + item.getTemplate().getPrice() + "]", canChoose(subject), new String[]{vendor.getName(), "sell"});
    }

}
