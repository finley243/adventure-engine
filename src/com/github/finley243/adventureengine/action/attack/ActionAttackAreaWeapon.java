package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionAttackAreaWeapon extends ActionAttackArea {

    public ActionAttackAreaWeapon(ItemWeapon weapon, Area area, String prompt) {
        super(weapon, area, prompt, weapon.getHitPhrase(), weapon.getHitRepeatPhrase(), weapon.getMissPhrase(), weapon.getMissRepeatPhrase(), weapon.getSkill(), 1, weapon.getRanges(), weapon.getRate(), weapon.getDamage(), weapon.getDamageType(), weapon.getArmorMult(), weapon.getTargetEffects(), 0.0f, !weapon.isRanged());
    }

}
