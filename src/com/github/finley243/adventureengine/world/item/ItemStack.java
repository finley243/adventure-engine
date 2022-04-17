package com.github.finley243.adventureengine.world.item;

public class ItemStack {

    private final Item item;
    private int count;

    public ItemStack(Item item) {
        this.item = item;
        this.count = 1;
    }

    public ItemStack(Item item, int count) {
        if(item.getTemplate().hasState() && count > 1) throw new IllegalArgumentException("Item " + item.getTemplate().getID() + " is not stackable");
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return item;
    }

    public boolean canStack() {
        return !item.getTemplate().hasState();
    }

    public int getCount() {
        return count;
    }

    public void addCount(int amount) {
        count += amount;
    }

}
