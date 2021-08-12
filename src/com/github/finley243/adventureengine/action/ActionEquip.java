package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionEquip implements Action {

	public static final float SUBOPTIMAL_WEAPON_UTILITY = 0.8f;
	public static final float OPTIMAL_WEAPON_UTILITY = 1.0f;
	
	private ItemWeapon item;
	
	public ActionEquip(ItemWeapon item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.setEquippedItem(item);
		subject.inventory().removeItem(item);
		Context context = new Context(subject, false, item, true);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("equip"), context));
	}

	@Override
	public String getPrompt() {
		return "Equip " + item.getFormattedName(false);
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
		return new MenuDataInventory("Equip", item);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionEquip)) {
            return false;
        } else {
            ActionEquip other = (ActionEquip) o;
            return other.item == this.item;
        }
    }

}
