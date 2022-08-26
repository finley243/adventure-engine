package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.item.ItemAmmo;
import com.github.finley243.adventureengine.textgen.LangUtils;
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
	private final ItemAmmo ammoType;
	
	public ActionWeaponReload(ItemWeapon weapon, ItemAmmo ammoType) {
		super(ActionDetectionChance.NONE);
		this.weapon = weapon;
		this.ammoType = ammoType;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.triggerScript("on_reload", subject);
		if (subject == subject.game().data().getPlayer()) {
			if (!ammoType.equals(weapon.getLoadedAmmoType()) && weapon.getAmmoRemaining() > 0) {
				subject.inventory().addItems(ammoType, weapon.getAmmoRemaining());
				weapon.emptyAmmo();
			}
			int ammoInInventory = subject.inventory().itemCount(ammoType);
			int reloadAmount = Math.min(weapon.reloadCapacity(), ammoInInventory);
			weapon.loadAmmo(reloadAmount);
			weapon.setLoadedAmmoType(ammoType);
			ammoType.onLoad(weapon);
			subject.inventory().removeItems(ammoType, reloadAmount);
		} else {
			weapon.loadAmmo(weapon.reloadCapacity());
		}
		Context context = new Context(new NounMapper().put("actor", subject).put("weapon", weapon).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("reload"), context, this, null, subject, null));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && (weapon.getAmmoFraction() < 1.0f || !ammoType.getTemplate().getID().equals(weapon.getLoadedAmmoType())) && (subject != subject.game().data().getPlayer() || subject.inventory().hasItem(ammoType.getTemplate().getID()));
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
		return new MenuData(LangUtils.titleCase(ammoType.getName()), canChoose(subject), new String[]{"attack", weapon.getName(), "reload (" + weapon.getAmmoRemaining() + "/" + weapon.getClipSize() + ")"});
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