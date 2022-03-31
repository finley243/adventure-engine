package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemApparel;

public class ActionItemDrop extends Action {

	private final Item item;

	public ActionItemDrop(Item item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.inventory().removeItem(item);
		subject.getArea().addObject(item);
		item.setArea(subject.getArea());
		Context context = new Context(subject, item);
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("drop"), context, this, subject));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		boolean isEquipped = subject.equipmentComponent().getEquippedItem() == item || item instanceof ItemApparel && subject.apparelComponent().getEquippedItems().contains(item);
		return new MenuData("Drop", canChoose(subject), new String[]{"inventory", item.getName() + (isEquipped ? " (equipped)" : subject.inventory().itemCountLabel(item.getTemplateID()))});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemDrop)) {
            return false;
        } else {
            ActionItemDrop other = (ActionItemDrop) o;
            return other.item == this.item;
        }
    }
	
}
