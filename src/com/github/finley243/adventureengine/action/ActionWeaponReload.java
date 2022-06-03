package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionWeaponReload extends Action {

	public static final float RELOAD_UTILITY_NONCOMBAT = 0.5f;
	public static final float RELOAD_UTILITY_COMBAT = 0.2f;
	public static final float RELOAD_UTILITY_COMBAT_EMPTY = 0.8f;

	private final ItemWeapon weapon;
	
	public ActionWeaponReload(ItemWeapon weapon) {
		this.weapon = weapon;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.triggerScript("on_reload");
		if(subject == subject.game().data().getPlayer()) {
			int ammoInInventory = subject.inventory().itemCount(weapon.getAmmoType());
			int reloadCapacity = weapon.reloadCapacity();
			if (ammoInInventory >= reloadCapacity) {
				weapon.loadAmmo(reloadCapacity);
				subject.inventory().removeItems(weapon.getAmmoType(), reloadCapacity);
			} else {
				weapon.loadAmmo(ammoInInventory);
				subject.inventory().removeItems(weapon.getAmmoType(), ammoInInventory);
			}
		} else {
			weapon.loadAmmo(weapon.reloadCapacity());
		}
		Context context = new Context(new NounMapper().put("actor", subject).put("weapon", weapon).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("reload"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && weapon.getAmmoFraction() < 1.0f && (subject != subject.game().data().getPlayer() || subject.inventory().hasItem(weapon.getAmmoType()));
	}

	@Override
	public float utility(Actor subject) {
		if(!subject.isInCombat()) {
			return (1.0f - weapon.getAmmoFraction()) * RELOAD_UTILITY_NONCOMBAT;
		} else {
			if(weapon.getAmmoFraction() == 0) {
				return RELOAD_UTILITY_COMBAT_EMPTY;
			} else {
				return (1.0f - weapon.getAmmoFraction()) * RELOAD_UTILITY_COMBAT;
			}
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Reload (" + weapon.getAmmoRemaining() + "/" + weapon.getClipSize() + ")", canChoose(subject), new String[]{weapon.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionWeaponReload)) {
            return false;
        } else {
            ActionWeaponReload other = (ActionWeaponReload) o;
            return other.weapon == this.weapon;
        }
    }

}