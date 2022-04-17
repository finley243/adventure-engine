package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.world.item.template.ItemTemplate;

public class ItemStack {

    private final ItemTemplate item;
    private final ItemState state;
    private int count;

    public ItemStack(ItemTemplate item, ItemState state) {
        this.item = item;
        this.state = state;
        this.count = 1;
    }

    public ItemStack(ItemTemplate item, int count) {
        if(item.hasState() && count > 1) throw new IllegalArgumentException("Item " + item.getID() + " is not stackable");
        this.item = item;
        this.state = null;
        this.count = count;
    }

    public ItemTemplate getItem() {
        return item;
    }

    public ItemState getState() {
        return state;
    }

    public int getCount() {
        return count;
    }

    public boolean canStack() {
        return !item.hasState();
    }

    public void addCount(int amount) {
        if(item.hasState()) throw new UnsupportedOperationException("Cannot modify count of " + item.getID() + " because it is not stackable");
        count += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemStack)) {
           return false;
        } else if (getItem() != ((ItemStack) o).getItem()) {
            return false;
        } else if (!getItem().hasState()) {
            // Stateless items will always match if the type is the same
            return true;
        } else {
            // Items with state will only match if they are the exact same instance (i.e. same state object)
            return getState() == ((ItemStack) o).getState();
        }
    }

    @Override
    public int hashCode() {
        return (31 * getItem().hashCode()) + (getState() == null ? 0 : getState().hashCode());
    }

}
