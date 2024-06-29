package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentAmmo;
import com.github.finley243.adventureengine.item.component.ItemComponentMagazine;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionWeaponReload extends Action {

	public static final float RELOAD_UTILITY_NONCOMBAT = 0.5f;
	public static final float RELOAD_UTILITY_COMBAT = 0.2f;
	public static final float RELOAD_UTILITY_COMBAT_EMPTY = 0.8f;

	private final Item weapon;
	private final Item ammoType;
	
	public ActionWeaponReload(Item weapon, Item ammoType) {
		this.weapon = weapon;
		this.ammoType = ammoType;
	}

	@Override
	public String getID() {
		return "item_reload";
	}

	@Override
	public Context getContext(Actor subject) {
		Context context = new Context(subject.game(), subject, null, weapon);
		context.setLocalVariable("ammo", Expression.constant(ammoType));
		return context;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.triggerScript("on_reload", new Context(subject.game(), subject, subject, weapon));
		if (subject == subject.game().data().getPlayer()) {
			if (!ammoType.equals(weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType()) && weapon.getComponentOfType(ItemComponentMagazine.class).getAmmoRemaining() > 0) {
				subject.getInventory().addItems(ammoType.getTemplateID(), weapon.getComponentOfType(ItemComponentMagazine.class).getAmmoRemaining());
				weapon.getComponentOfType(ItemComponentMagazine.class).emptyAmmo();
			}
			int ammoInInventory = subject.getInventory().itemCount(ammoType);
			int reloadAmount = Math.min(weapon.getComponentOfType(ItemComponentMagazine.class).reloadCapacity(), ammoInInventory);
			weapon.getComponentOfType(ItemComponentMagazine.class).loadAmmo(reloadAmount);
			weapon.getComponentOfType(ItemComponentMagazine.class).setLoadedAmmoType(ammoType);
			ammoType.getComponentOfType(ItemComponentAmmo.class).onLoad(weapon);
			subject.getInventory().removeItems(ammoType.getTemplateID(), reloadAmount);
		} else {
			weapon.getComponentOfType(ItemComponentMagazine.class).loadAmmo(weapon.getComponentOfType(ItemComponentMagazine.class).reloadCapacity());
			weapon.getComponentOfType(ItemComponentMagazine.class).setLoadedAmmoType(ammoType);
		}
		Context context = new Context(subject.game(), subject, null, weapon);
		SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("reload"), context, true, this, null));
	}

	@Override
	public CanChooseResult canChoose(Actor subject) {
		CanChooseResult resultSuper = super.canChoose(subject);
		if (!resultSuper.canChoose()) {
			return resultSuper;
		}
		if (weapon.getComponentOfType(ItemComponentMagazine.class).getAmmoFraction() >= 1.0f && weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType() != null && weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType().getTemplateID().equals(ammoType.getTemplateID())) {
			return new CanChooseResult(false, "Ammo already loaded");
		}
		if (subject.isPlayer() && !subject.getInventory().hasItem(ammoType.getTemplateID())) {
			return new CanChooseResult(false, "No ammo in inventory");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public int actionPoints(Actor subject) {
		return weapon.getComponentOfType(ItemComponentMagazine.class).getReloadActionPoints(new Context(subject.game(), subject, subject, weapon, new MapBuilder<String, Expression>().put("ammo_type", new ExpressionConstantString(ammoType.getTemplateID())).build()));
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isInCombat()) {
			return (1.0f - weapon.getComponentOfType(ItemComponentMagazine.class).getAmmoFraction()) * RELOAD_UTILITY_NONCOMBAT;
		} else {
			if (weapon.getComponentOfType(ItemComponentMagazine.class).getAmmoFraction() == 0) {
				return RELOAD_UTILITY_COMBAT_EMPTY;
			} else {
				return (1.0f - weapon.getComponentOfType(ItemComponentMagazine.class).getAmmoFraction()) * RELOAD_UTILITY_COMBAT;
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