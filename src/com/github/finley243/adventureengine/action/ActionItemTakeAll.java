package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.PluralNoun;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemWeapon;
import com.github.finley243.adventureengine.world.object.ObjectItem;

public class ActionItemTakeAll extends Action {

	private final ObjectItem objectItem;

	public ActionItemTakeAll(ObjectItem objectItem) {
		this.objectItem = objectItem;
	}
	
	@Override
	public void choose(Actor subject) {
		int count = objectItem.getCount();
		Item item = Item.objectToItem(subject.game(), objectItem, count);
		subject.inventory().addItems(item, count);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", new PluralNoun(objectItem, count)).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, this, subject));
	}

	@Override
	public float utility(Actor subject) {
		if(objectItem.getItem() instanceof ItemWeapon && subject.isInCombat() && !subject.hasWeapon()) {
			return 0.7f;
		} else {
			return 0.0f;
		}
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		int count = objectItem.getCount();
		return new MenuData("Take all", canChoose(subject), new String[]{objectItem.getName() + (count > 1 ? " (" + count + ")" : "")});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemTakeAll)) {
            return false;
        } else {
            ActionItemTakeAll other = (ActionItemTakeAll) o;
            return other.objectItem == this.objectItem;
        }
    }
	
}
