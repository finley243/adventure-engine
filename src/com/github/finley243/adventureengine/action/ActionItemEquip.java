package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionItemEquip extends Action {

	public static final float SUBOPTIMAL_WEAPON_UTILITY = 0.4f;
	public static final float OPTIMAL_WEAPON_UTILITY = 0.7f;

	private final ItemEquippable item;
	
	public ActionItemEquip(ItemEquippable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.getEquipmentComponent().equip(item);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", item).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("equip"), context, this, null, subject, null));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && !subject.getEquipmentComponent().hasEquippedItem();
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
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Equip", canChoose(subject), new String[]{"inventory", item.getName() + subject.getInventory().itemCountLabel(item)}, new String[]{"equip " + item.getName(), "pull out " + item.getName(), "take out " + item.getName()});
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
