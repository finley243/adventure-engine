package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReload implements Action {

	public static final float RELOAD_UTILITY_NONCOMBAT = 0.5f;
	public static final float RELOAD_UTILITY_COMBAT = 0.6f;
	public static final float RELOAD_UTILITY_COMBAT_EMPTY = 0.8f;

	private ItemWeapon weapon;
	
	public ActionReload(ItemWeapon weapon) {
		this.weapon = weapon;
	}
	
	@Override
	public void choose(Actor subject) {
		weapon.reloadFull();
		Context context = new Context(subject, false, weapon, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("reload"), context));
	}

	@Override
	public String getPrompt() {
		return "Reload " + weapon.getName();
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
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}

	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataEquipped("Reload", weapon);
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