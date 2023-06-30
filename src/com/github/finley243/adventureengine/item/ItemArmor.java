package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.ArmorTemplate;

import java.util.Set;

public class ItemArmor extends ItemEquippable {

    public ItemArmor(Game game, String ID, String templateID) {
        super(game, ID, templateID);
    }

    private ArmorTemplate getArmorTemplate() {
        return (ArmorTemplate) getTemplate();
    }

    public int getDamageResistance(String damageType) {
        return getArmorTemplate().getDamageResistance(damageType);
    }

    public float getDamageMult(String damageType) {
        return getArmorTemplate().getDamageMult(damageType);
    }

    public Set<String> getCoveredLimbs() {
        return getArmorTemplate().getCoveredLimbs();
    }

    public boolean coversMainBody() {
        return getArmorTemplate().coversMainBody();
    }

}
