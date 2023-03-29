package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionVendorBuy;
import com.github.finley243.adventureengine.action.ActionVendorSell;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VendorComponent {

    private static final float SELL_PRICE_MULTIPLIER = 0.8f;

    private final Actor vendor;
    private final Inventory vendorInventory;
    private final String lootTable;

    private final Set<String> buyTags;
    private final boolean buyAll;

    private boolean enabled;

    public VendorComponent(Actor vendor) {
        this.vendorInventory = new Inventory(vendor.game(), null);
        this.vendor = vendor;
        this.lootTable = vendor.getTemplate().getVendorLootTable();
        this.buyTags = vendor.getTemplate().vendorBuyTags();
        this.buyAll = vendor.getTemplate().vendorBuyAll();
        this.enabled = !vendor.getTemplate().vendorStartDisabled();
    }

    public void generateInventory() {
        vendorInventory.clear();
        vendorInventory.addItems(vendor.game().data().getLootTable(lootTable).generateItems(vendor.game()));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        if (!enabled) return actions;
        for (Item item : vendorInventory.getItems()) {
            actions.add(new ActionVendorBuy(vendor, vendorInventory, item, item.getTemplate().getPrice()));
        }
        if (!buyTags.isEmpty() || buyAll) {
            for (Item item : subject.getInventory().getItems()) {
                boolean canBuy = buyAll;
                if (!canBuy) {
                    // TODO - Optimize checks?
                    for (String buyTag : buyTags) {
                        if (item.getTemplate().getTags().contains(buyTag)) {
                            canBuy = true;
                            break;
                        }
                    }
                }
                if(canBuy) {
                    actions.add(new ActionVendorSell(vendor, vendorInventory, item, (int) ( item.getTemplate().getPrice() * SELL_PRICE_MULTIPLIER)));
                }
            }
        }
        return actions;
    }

}
