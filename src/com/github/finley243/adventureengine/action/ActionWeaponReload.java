package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentAmmo;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;

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
	public void choose(Actor subject, int repeatActionCount) {
		subject.triggerScript("on_reload", new Context(subject.game(), subject, subject, weapon));
		if (subject == subject.game().data().getPlayer()) {
			if (!ammoType.equals(weapon.getComponentOfType(ItemComponentWeapon.class).getLoadedAmmoType()) && weapon.getComponentOfType(ItemComponentWeapon.class).getAmmoRemaining() > 0) {
				subject.getInventory().addItems(ammoType.getTemplateID(), weapon.getComponentOfType(ItemComponentWeapon.class).getAmmoRemaining());
				weapon.getComponentOfType(ItemComponentWeapon.class).emptyAmmo();
			}
			int ammoInInventory = subject.getInventory().itemCount(ammoType);
			int reloadAmount = Math.min(weapon.getComponentOfType(ItemComponentWeapon.class).reloadCapacity(), ammoInInventory);
			System.out.println("Reload capacity: " + weapon.getComponentOfType(ItemComponentWeapon.class).reloadCapacity());
			System.out.println("Ammo in inventory: " + ammoInInventory);
			System.out.println("Reload amount: " + reloadAmount);
			weapon.getComponentOfType(ItemComponentWeapon.class).loadAmmo(reloadAmount);
			weapon.getComponentOfType(ItemComponentWeapon.class).setLoadedAmmoType(ammoType);
			ammoType.getComponentOfType(ItemComponentAmmo.class).onLoad(weapon);
			subject.getInventory().removeItems(ammoType.getTemplateID(), reloadAmount);
		} else {
			weapon.getComponentOfType(ItemComponentWeapon.class).loadAmmo(weapon.getComponentOfType(ItemComponentWeapon.class).reloadCapacity());
			weapon.getComponentOfType(ItemComponentWeapon.class).setLoadedAmmoType(ammoType);
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
		if (weapon.getComponentOfType(ItemComponentWeapon.class).getAmmoFraction() >= 1.0f && weapon.getComponentOfType(ItemComponentWeapon.class).getLoadedAmmoType() != null && weapon.getComponentOfType(ItemComponentWeapon.class).getLoadedAmmoType().getTemplateID().equals(ammoType.getTemplateID())) {
			return new CanChooseResult(false, "Ammo already loaded");
		}
		if (subject.isPlayer() && !subject.getInventory().hasItem(ammoType.getTemplateID())) {
			return new CanChooseResult(false, "No ammo in inventory");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public int actionPoints(Actor subject) {
		return weapon.getComponentOfType(ItemComponentWeapon.class).getReloadActionPoints(new Context(subject.game(), subject, subject, weapon, new MapBuilder<String, Expression>().put("ammo_type", new ExpressionConstantString(ammoType.getTemplateID())).build()));
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isInCombat()) {
			return (1.0f - weapon.getComponentOfType(ItemComponentWeapon.class).getAmmoFraction()) * RELOAD_UTILITY_NONCOMBAT;
		} else {
			if (weapon.getComponentOfType(ItemComponentWeapon.class).getAmmoFraction() == 0) {
				return RELOAD_UTILITY_COMBAT_EMPTY;
			} else {
				return (1.0f - weapon.getComponentOfType(ItemComponentWeapon.class).getAmmoFraction()) * RELOAD_UTILITY_COMBAT;
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