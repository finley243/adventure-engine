package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionItemTakeAll extends Action {

	private final Area area;
	private final Item item;

	public ActionItemTakeAll(Area area, Item item) {
		super(ActionDetectionChance.LOW);
		this.area = area;
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		//int count = objectItem.getCount();
		//Item item = Item.objectToItem(subject.game(), objectItem, count);
		int count = area.getInventory().itemCount(item);
		area.getInventory().removeItems(item, count);
		subject.inventory().addItems(item, count);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", new PluralNoun(item, count)).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, this, null, subject, null));
	}

	@Override
	public float utility(Actor subject) {
		if(item instanceof ItemWeapon && subject.isInCombat() && !subject.hasWeapon()) {
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
	public MenuChoice getMenuChoices(Actor subject) {
		int count = area.getInventory().itemCount(item);
		return new MenuChoice("Take all", canChoose(subject), new String[]{"ground", item.getName() + (count > 1 ? " (" + count + ")" : "")}, new String[]{"take all " + LangUtils.pluralizeNoun(item.getName()), "pick up all " + LangUtils.pluralizeNoun(item.getName()), "pickup all " + LangUtils.pluralizeNoun(item.getName())});
	}

	@Override
	public ActionResponseType responseType() {
		/*if (objectItem.isStealing()) {
			return ActionResponseType.STEAL;
		}*/
		return ActionResponseType.NONE;
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemTakeAll)) {
            return false;
        } else {
            ActionItemTakeAll other = (ActionItemTakeAll) o;
            return other.item == this.item;
        }
    }
	
}
