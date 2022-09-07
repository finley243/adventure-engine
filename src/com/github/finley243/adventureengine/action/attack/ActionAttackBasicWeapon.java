package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;

public class ActionAttackBasicWeapon extends ActionAttackBasic {

    public ActionAttackBasicWeapon(ItemWeapon weapon, AttackTarget target, String prompt) {
        super(weapon, target, prompt, weapon.getHitPhrase(), weapon.getHitRepeatPhrase(), weapon.getMissPhrase(), weapon.getMissRepeatPhrase(), weapon.getSkill(), 1, weapon.getRange(), weapon.getRate(), weapon.getDamage(), weapon.getDamageType(), weapon.getArmorMult(), 0.0f, !weapon.isRanged());
    }

}
