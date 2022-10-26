package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemEquippable;

public class ActionItemUnequip extends Action {

	private final ItemEquippable item;
	
	public ActionItemUnequip(ItemEquippable item) {
		super(ActionDetectionChance.NONE);
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.equipmentComponent().unequip(item);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", item).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("unequip"), context, this, null, subject, null));
	}

	@Override
	public float utility(Actor subject) {
		if(!subject.isInCombat()) {
			return 0.4f;
		}
		return 0.0f;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Unequip", canChoose(subject), new String[]{"inventory", item.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemUnequip)) {
            return false;
        } else {
            ActionItemUnequip other = (ActionItemUnequip) o;
            return other.item == this.item;
        }
    }
	
}
