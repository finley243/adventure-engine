package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;

public class ActionAttackLimbWeapon extends ActionAttackLimb {

    public ActionAttackLimbWeapon(ItemWeapon weapon, AttackTarget target, Limb limb, String prompt) {
        super(weapon, target, limb, prompt, weapon.getLimbHitPhrase(), weapon.getLimbHitRepeatPhrase(), weapon.getLimbMissPhrase(), weapon.getLimbMissRepeatPhrase(), weapon.getSkill(), 1, weapon.getRanges(), weapon.getRate(), weapon.getDamage(), weapon.getDamageType(), weapon.getArmorMult(), weapon.getTargetEffects(), 0.0f, !weapon.isRanged());
    }

}
