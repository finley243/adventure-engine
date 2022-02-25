package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionVendorBuy;
import com.github.finley243.adventureengine.action.ActionVendorSell;
import com.github.finley243.adventureengine.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class VendorComponent {

    private final Actor vendor;
    private final Inventory vendorInventory;
    private final String lootTable;
    private final boolean canBuy;

    private boolean enabled;

    public VendorComponent(Actor vendor, String lootTable, boolean canBuy, boolean startDisabled) {
        this.vendorInventory = new Inventory();
        this.vendor = vendor;
        this.lootTable = lootTable;
        this.canBuy = canBuy;
        this.enabled = !startDisabled;
        generateInventory();
    }

    public void generateInventory() {
        vendorInventory.clear();
        vendorInventory.addItems(Data.getLootTable(lootTable).generateItems());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        for(Item item : vendorInventory.getUniqueItems()) {
            actions.add(new ActionVendorBuy(vendor, vendorInventory, item));
        }
        if(canBuy) {
            for (Item item : subject.inventory().getUniqueItems()) {
                actions.add(new ActionVendorSell(vendor, vendorInventory, item));
            }
        }
        return actions;
    }

}
