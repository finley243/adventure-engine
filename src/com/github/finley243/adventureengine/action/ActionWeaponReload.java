package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.item.ItemAmmo;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;

public class ActionWeaponReload extends Action {

	public static final float RELOAD_UTILITY_NONCOMBAT = 0.5f;
	public static final float RELOAD_UTILITY_COMBAT = 0.2f;
	public static final float RELOAD_UTILITY_COMBAT_EMPTY = 0.8f;

	private final ItemWeapon weapon;
	private final ItemAmmo ammoType;
	
	public ActionWeaponReload(ItemWeapon weapon, ItemAmmo ammoType) {
		this.weapon = weapon;
		this.ammoType = ammoType;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.triggerScript("on_reload", new Context(subject.game(), subject, subject, weapon));
		if (subject == subject.game().data().getPlayer()) {
			if (!ammoType.equals(weapon.getLoadedAmmoType()) && weapon.getAmmoRemaining() > 0) {
				subject.getInventory().addItems(ammoType.getTemplateID(), weapon.getAmmoRemaining());
				weapon.emptyAmmo();
			}
			int ammoInInventory = subject.getInventory().itemCount(ammoType);
			int reloadAmount = Math.min(weapon.reloadCapacity(), ammoInInventory);
			weapon.loadAmmo(reloadAmount);
			weapon.setLoadedAmmoType(ammoType);
			ammoType.onLoad(weapon);
			subject.getInventory().removeItems(ammoType.getTemplateID(), reloadAmount);
		} else {
			weapon.loadAmmo(weapon.reloadCapacity());
			weapon.setLoadedAmmoType(ammoType);
		}
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("weapon", weapon).build());
		SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("reload"), context, true, this, null, subject, null));
	}

	@Override
	public CanChooseResult canChoose(Actor subject) {
		CanChooseResult resultSuper = super.canChoose(subject);
		if (!resultSuper.canChoose()) {
			return resultSuper;
		}
		if (weapon.getAmmoFraction() >= 1.0f && weapon.getLoadedAmmoType() != null && weapon.getLoadedAmmoType().getTemplateID().equals(ammoType.getTemplateID())) {
			return new CanChooseResult(false, "Ammo already loaded");
		}
		if (subject.isPlayer() && !subject.getInventory().hasItem(ammoType.getTemplateID())) {
			return new CanChooseResult(false, "No ammo in inventory");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public int actionPoints(Actor subject) {
		return weapon.getReloadActionPoints(new Context(subject.game(), subject, subject, weapon, new MapBuilder<String, Expression>().put("ammo_type", new ExpressionConstantString(ammoType.getTemplateID())).build()));
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isInCombat()) {
			return (1.0f - weapon.getAmmoFraction()) * RELOAD_UTILITY_NONCOMBAT;
		} else {
			if (weapon.getAmmoFraction() == 0) {
				return RELOAD_UTILITY_COMBAT_EMPTY;
			} else {
				return (1.0f - weapon.getAmmoFraction()) * RELOAD_UTILITY_COMBAT;
			}
		}
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataInventoryCombine(weapon, subject.getInventory(), ammoType, subject.getInventory());
	}

	@Override
	public String getPrompt(Actor subject) {
		return "Reload";
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionWeaponReload other)) {
            return false;
        } else {
			return other.weapon == this.weapon;
        }
    }

}