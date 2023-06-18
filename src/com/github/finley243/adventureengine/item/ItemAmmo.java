package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.AmmoTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

public class ItemAmmo extends Item {

    private final String templateID;

    public ItemAmmo(Game game, String ID, String templateID) {
        super(game, ID);
        this.templateID = templateID;
    }

    @Override
    public ItemTemplate getTemplate() {
        return getAmmoTemplate();
    }

    public AmmoTemplate getAmmoTemplate() {
        return (AmmoTemplate) game().data().getItem(templateID);
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
