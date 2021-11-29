package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReload extends Action {

	public static final float RELOAD_UTILITY_NONCOMBAT = 0.5f;
	public static final float RELOAD_UTILITY_COMBAT = 0.6f;
	public static final float RELOAD_UTILITY_COMBAT_EMPTY = 0.8f;

	private final ItemWeapon weapon;
	
	public ActionReload(ItemWeapon weapon) {
		this.weapon = weapon;
	}
	
	@Override
	public void choose(Actor subject) {
		weapon.reloadFull();
		Context context = new Context(subject, false, weapon, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("reload"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && weapon.getAmmoFraction() < 1.0f;
	}

	@Override
	public float utility(Actor subject) {
		if(!subject.isInCombat()) {
			return RELOAD_UTILITY_NONCOMBAT;
		} else {
			if(weapon.getAmmoFraction() == 0) {
				return RELOAD_UTILITY_COMBAT_EMPTY;
			} else {
				return RELOAD_UTILITY_COMBAT;
			}
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Reload (" + weapon.getAmmoRemaining() + ")", "Reload " + weapon.getFormattedName(false), canChoose(subject), new String[]{weapon.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionReload)) {
            return false;
        } else {
            ActionReload other = (ActionReload) o;
            return other.weapon == this.weapon;
        }
    }

}