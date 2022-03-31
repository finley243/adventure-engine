package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemEquippable;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionItemEquip extends Action {

	public static final float SUBOPTIMAL_WEAPON_UTILITY = 0.8f;
	public static final float OPTIMAL_WEAPON_UTILITY = 1.0f;

	private final ItemEquippable item;
	
	public ActionItemEquip(ItemEquippable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.equipmentComponent().setEquippedItem(item);
		Context context = new Context(subject, item);
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("equip"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && !subject.equipmentComponent().hasEquippedItem();
	}

	@Override
	public float utility(Actor subject) {
		if(item instanceof ItemWeapon) {
			ItemWeapon weapon = (ItemWeapon) item;
			if (!subject.isInCombat()) return 0;
			if (weapon.isRanged()) {
				if (subject.hasMeleeTargets()) {
					return SUBOPTIMAL_WEAPON_UTILITY;
				} else {
					return OPTIMAL_WEAPON_UTILITY;
				}
			} else {
				if (subject.hasMeleeTargets()) {
					return OPTIMAL_WEAPON_UTILITY;
				} else {
					return SUBOPTIMAL_WEAPON_UTILITY;
				}
			}
		} else {
			return 0.0f;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Equip", canChoose(subject), new String[]{"inventory", item.getName() + subject.inventory().itemCountLabel(item.getTemplateID())});
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
