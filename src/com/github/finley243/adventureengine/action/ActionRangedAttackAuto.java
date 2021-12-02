package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionRangedAttackAuto extends ActionAttack {

	public static final int AMMO_USED = 6;

	public ActionRangedAttackAuto(ItemWeapon weapon, Actor target) {
		super(weapon, target);
	}

	@Override
	public void choose(Actor subject) {
		if(getWeapon().isRanged()) {
			getWeapon().consumeAmmo(AMMO_USED);
			Game.EVENT_BUS.post(new SoundEvent(subject.getArea(), true));
		}
		CombatHelper.handleAttack(subject, getTarget(), getWeapon(), null, true);
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && getWeapon().getAmmoRemaining() >= AMMO_USED && subject.canSee(getTarget());
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isCombatTarget(getTarget())) return 0;
		return 0.8f;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Attack (" + LangUtils.titleCase(getWeapon().getName()) + ", Autofire)", "Attack " + getTarget().getFormattedName(false) + " with " + getWeapon().getFormattedName(false), canChoose(subject), new String[]{getTarget().getName()});
	}

}
