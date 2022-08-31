package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionAttackBasicWeapon extends ActionAttackBasic {

    public ActionAttackBasicWeapon(ItemWeapon weapon, Actor target, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat) {
        super(weapon, target, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, 1, weapon.getRange(), weapon.getRate(), weapon.getDamage(), weapon.getDamageType(), weapon.getArmorMult(), 0.0f, !weapon.isRanged());
    }

}
