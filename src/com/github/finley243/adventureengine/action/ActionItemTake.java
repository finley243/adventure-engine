package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.object.ObjectItem;

public class ActionItemTake extends Action {

	private final ObjectItem objectItem;
	
	public ActionItemTake(ObjectItem objectItem) {
		super(ActionDetectionChance.LOW);
		this.objectItem = objectItem;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Item item = Item.objectToItem(subject.game(), objectItem, 1);
		subject.inventory().addItem(item);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", objectItem).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("pickUp"), context, this, null, subject, null));
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
	public MenuChoice getMenuChoices(Actor subject) {
		int count = objectItem.getCount();
		return new MenuChoice("Take", canChoose(subject), new String[]{objectItem.getName() + (count > 1 ? " (" + count + ")" : "")});
	}

	@Override
	public ActionResponseType responseType() {
		if (objectItem.isStealing()) {
			return ActionResponseType.STEAL;
		}
		return ActionResponseType.NONE;
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemTake)) {
            return false;
        } else {
            ActionItemTake other = (ActionItemTake) o;
            return other.objectItem == this.objectItem;
        }
    }
	
}
