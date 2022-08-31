package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionAttackAreaWeapon extends ActionAttackArea {

    public ActionAttackAreaWeapon(ItemWeapon weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat) {
        super(weapon, area, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, 1, weapon.getRange(), weapon.getRate(), weapon.getDamage(), weapon.getDamageType(), weapon.getArmorMult(), 0.0f, !weapon.isRanged());
    }

}
