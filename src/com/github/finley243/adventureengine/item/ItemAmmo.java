package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.AmmoTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

public class ItemAmmo extends Item {

    public ItemAmmo(Game game, String ID, String templateID) {
        super(game, ID, templateID);
    }

    private AmmoTemplate getAmmoTemplate() {
        return (AmmoTemplate) getTemplate();
    }

    public boolean isReusable() {
        return getAmmoTemplate().isReusable();
    }

    public void onLoad(ItemWeapon weapon) {
        for (String effectID : getAmmoTemplate().getWeaponEffects()) {
            weapon.getEffectComponent().addEffect(effectID);
        }
    }

    public void onUnload(ItemWeapon weapon) {
        for (String effectID : getAmmoTemplate().getWeaponEffects()) {
            weapon.getEffectComponent().removeEffect(effectID);
        }
    }

}
