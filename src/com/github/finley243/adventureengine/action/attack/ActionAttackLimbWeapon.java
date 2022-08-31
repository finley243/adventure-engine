package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionAttackLimbWeapon extends ActionAttackLimb {

    public ActionAttackLimbWeapon(ItemWeapon weapon, Actor target, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat) {
        super(weapon, target, limb, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, 1, weapon.getRange(), weapon.getRate(), weapon.getDamage(), weapon.getDamageType(), weapon.getArmorMult(), 0.0f, !weapon.isRanged());
    }

}