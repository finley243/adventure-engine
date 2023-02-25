package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.*;

public class ActionItemDropAll extends Action {

	private final Item item;

	public ActionItemDropAll(Item item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		int count = subject.getInventory().itemCount(item);
		subject.getInventory().removeItems(item, count);
		//Item.itemToObject(subject.game(), item, count, subject.getArea());
		subject.getArea().getInventory().addItems(item, count);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", new PluralNoun(item, count)).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("drop"), context, this, null, subject, null));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Drop all", canChoose(subject), new String[]{"inventory", item.getName() + subject.getInventory().itemCountLabel(item)}, new String[]{"drop all " + LangUtils.pluralizeNoun(item.getName())});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemDropAll)) {
            return false;
        } else {
            ActionItemDropAll other = (ActionItemDropAll) o;
            return other.item == this.item;
        }
    }
	
}
