package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.Item;

public class ActionItemDrop extends Action {

	private final Item item;

	public ActionItemDrop(Item item) {
		super(ActionDetectionChance.LOW);
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.inventory().removeItem(item);
		Item.itemToObject(subject.game(), item, 1, subject.getArea());
		Context context = new Context(new NounMapper().put("actor", subject).put("item", item).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("drop"), context, this, subject));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Drop", canChoose(subject), new String[]{"inventory", item.getName() + subject.inventory().itemCountLabel(item)});
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
