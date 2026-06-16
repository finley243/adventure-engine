package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.MagazineItemComponent;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionWeaponReload extends Action {

	public static final float RELOAD_UTILITY_NONCOMBAT = 0.5f;
	public static final float RELOAD_UTILITY_COMBAT = 0.2f;
	public static final float RELOAD_UTILITY_COMBAT_EMPTY = 0.8f;

	private final Item weapon;
	private final ItemTemplate ammoType;
	
	public ActionWeaponReload(ActionDependencies dependencies, Item weapon, ItemTemplate ammoType) {
        super(dependencies);
        this.weapon = weapon;
		this.ammoType = ammoType;
	}

	@Override
	public String getID() {
		return "item_reload";
	}

	@Override
	public Context getContext(Actor subject) {
		Context context = Context.builder().subject(subject).parentItem(weapon).build();
		context.setLocalVariable("ammo", Expression.valueHolder(ammoType));
		return context;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.triggerScript("on_reload", Context.builder().subject(subject).target(subject).parentItem(weapon).build());
		if (subject.isPlayer()) {
			if (!ammoType.equals(weapon.getComponentOfType(MagazineItemComponent.class).getLoadedAmmoType()) && weapon.getComponentOfType(MagazineItemComponent.class).getAmmoRemaining() > 0) {
				subject.getInventory().addItems(ammoType.getID(), weapon.getComponentOfType(MagazineItemComponent.class).getAmmoRemaining());
				weapon.getComponentOfType(MagazineItemComponent.class).emptyAmmo();
			}
			int ammoInInventory = subject.getInventory().itemCount(ammoType.getID());
			int reloadAmount = Math.min(weapon.getComponentOfType(MagazineItemComponent.class).reloadCapacity(), ammoInInventory);
			weapon.getComponentOfType(MagazineItemComponent.class).loadAmmo(reloadAmount);
			weapon.getComponentOfType(MagazineItemComponent.class).setLoadedAmmoType(ammoType);
			subject.getInventory().removeItems(ammoType.getID(), reloadAmount);
		} else {
			weapon.getComponentOfType(MagazineItemComponent.class).loadAmmo(weapon.getComponentOfType(MagazineItemComponent.class).reloadCapacity());
			weapon.getComponentOfType(MagazineItemComponent.class).setLoadedAmmoType(ammoType);
		}
		Context context = Context.builder().subject(subject).parentItem(weapon).build();
		sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get("reload"), context, true, this, null));
	}

	@Override
	public CanChooseResult canChoose(Actor subject) {
		CanChooseResult resultSuper = super.canChoose(subject);
		if (!resultSuper.canChoose()) {
			return resultSuper;
		}
		if (weapon.getComponentOfType(MagazineItemComponent.class).getAmmoFraction() >= 1.0f && weapon.getComponentOfType(MagazineItemComponent.class).getLoadedAmmoType() != null && weapon.getComponentOfType(MagazineItemComponent.class).getLoadedAmmoType().equals(ammoType)) {
			return new CanChooseResult(false, "Ammo already loaded");
		}
		if (subject.isPlayer() && !subject.getInventory().hasItem(ammoType.getID())) {
			return new CanChooseResult(false, "No ammo in inventory");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public int actionPoints(Actor subject) {
		Context context = Context.builder().subject(subject).target(subject).parentItem(weapon).parentAction(this).addVariable("ammo_type", Expression.valueHolder(ammoType)).build();
		return weapon.getComponentOfType(MagazineItemComponent.class).getReloadActionPoints(context);
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isInCombat()) {
			return (1.0f - weapon.getComponentOfType(MagazineItemComponent.class).getAmmoFraction()) * RELOAD_UTILITY_NONCOMBAT;
		} else {
			if (weapon.getComponentOfType(MagazineItemComponent.class).getAmmoFraction() == 0) {
				return RELOAD_UTILITY_COMBAT_EMPTY;
			} else {
				return (1.0f - weapon.getComponentOfType(MagazineItemComponent.class).getAmmoFraction()) * RELOAD_UTILITY_COMBAT;
			}
		}
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataInventoryCombine(weapon, subject.getInventory(), null, ammoType, subject.getInventory());
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