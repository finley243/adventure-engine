package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
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
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("equip"), context, this, null, subject, null));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && !subject.getEquipmentComponent().hasEquippedItem();
	}

	@Override
	public float utility(Actor subject) {
		if (item instanceof ItemWeapon weapon) {
			if (!subject.isInCombat()) return 0;
			if (weapon.isRanged()) {
				if (UtilityUtils.actorHasMeleeTargets(subject)) {
					return SUBOPTIMAL_WEAPON_UTILITY;
				} else {
					return OPTIMAL_WEAPON_UTILITY;
				}
			} else {
				if (UtilityUtils.actorHasMeleeTargets(subject)) {
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
        if (!(o instanceof ActionItemEquip other)) {
            return false;
        } else {
			return other.item == this.item;
        }
    }

}
