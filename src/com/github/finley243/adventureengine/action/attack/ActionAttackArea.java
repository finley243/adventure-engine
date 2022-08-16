package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionAttackArea extends ActionAttack {

    private final Area area;

    public ActionAttackArea(ItemWeapon weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, int ammoConsumed, boolean overrideWeaponRate, float damageMult, float hitChanceMult, boolean canDodge) {
        super(weapon, area.getAttackTargets(), null, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, ammoConsumed, overrideWeaponRate, damageMult, hitChanceMult, canDodge);
        this.area = area;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(getPrompt() + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{getWeapon().getName(), area.getName()});
    }

}
