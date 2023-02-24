package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.Item;

public class ActionVendorBuy extends Action {

    private final Actor vendor;
    private final Inventory vendorInventory;
    private final Item item;
    private final int price;

    public ActionVendorBuy(Actor vendor, Inventory vendorInventory, Item item, int price) {
        super(ActionDetectionChance.HIGH);
        this.vendor = vendor;
        this.vendorInventory = vendorInventory;
        this.item = item;
        this.price = price;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        vendorInventory.removeItem(item);
        subject.adjustMoney(-price);
        subject.getInventory().addItem(item);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", item).put("vendor", vendor).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("buy"), context, this, null, subject, null));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && subject.getMoney() >= item.getTemplate().getPrice();
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice(LangUtils.titleCase(item.getName()) + " [" + item.getTemplate().getPrice() + "]", canChoose(subject), new String[]{vendor.getName(), "buy"}, new String[]{"buy " + item.getName() + " from " + vendor.getName()});
    }

}
