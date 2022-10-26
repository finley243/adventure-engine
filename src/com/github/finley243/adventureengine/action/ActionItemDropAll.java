package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.PluralNoun;
import com.github.finley243.adventureengine.item.Item;

public class ActionItemDropAll extends Action {

	private final Item item;

	public ActionItemDropAll(Item item) {
		super(ActionDetectionChance.LOW);
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		int count = subject.inventory().itemCount(item);
		subject.inventory().removeItems(item, count);
		Item.itemToObject(subject.game(), item, count, subject.getArea());
		Context context = new Context(new NounMapper().put("actor", subject).put("item", new PluralNoun(item, count)).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("drop"), context, this, null, subject, null));
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Drop all", canChoose(subject), new String[]{"inventory", item.getName() + subject.inventory().itemCountLabel(item)});
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
