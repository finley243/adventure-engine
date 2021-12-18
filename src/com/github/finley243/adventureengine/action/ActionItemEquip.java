package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionItemEquip extends Action {

	public static final float SUBOPTIMAL_WEAPON_UTILITY = 0.8f;
	public static final float OPTIMAL_WEAPON_UTILITY = 1.0f;

	private final ItemWeapon item;
	
	public ActionItemEquip(ItemWeapon item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.setEquippedItem(item);
		subject.inventory().removeItem(item);
		Context context = new Context(subject, false, item, true);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("equip"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && !subject.hasEquippedItem();
	}

	@Override
	public float utility(Actor subject) {
		if(!subject.isInCombat()) return 0;
		if(item.isRanged()) {
			if(subject.hasMeleeTargets()) {
				return SUBOPTIMAL_WEAPON_UTILITY;
			} else {
				return OPTIMAL_WEAPON_UTILITY;
			}
		} else {
			if(subject.hasMeleeTargets()) {
				return OPTIMAL_WEAPON_UTILITY;
			} else {
				return SUBOPTIMAL_WEAPON_UTILITY;
			}
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		int count = subject.inventory().itemCountWithID(item.getStatsID());
		return new MenuData("Equip", "Equip " + item.getFormattedName(false), canChoose(subject), new String[]{"inventory", item.getName() + (count > 1 ? "(" + count + ")" : "")});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemEquip)) {
            return false;
        } else {
            ActionItemEquip other = (ActionItemEquip) o;
            return other.item == this.item;
        }
    }

}
