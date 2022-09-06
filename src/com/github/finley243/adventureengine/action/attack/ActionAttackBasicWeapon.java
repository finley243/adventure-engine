package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionAttackBasicWeapon extends ActionAttackBasic {

    public ActionAttackBasicWeapon(ItemWeapon weapon, Actor target, String prompt) {
        super(weapon, target, prompt, weapon.getHitPhrase(), weapon.getHitRepeatPhrase(), weapon.getMissPhrase(), weapon.getMissRepeatPhrase(), 1, weapon.getRange(), weapon.getRate(), weapon.getDamage(), weapon.getDamageType(), weapon.getArmorMult(), 0.0f, !weapon.isRanged());
    }

}
