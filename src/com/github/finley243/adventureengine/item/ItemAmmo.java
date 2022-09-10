package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.template.AmmoTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

public class ItemAmmo extends Item {

    private final AmmoTemplate stats;

    public ItemAmmo(Game game, String ID, AmmoTemplate stats) {
        super(game, ID);
        this.stats = stats;
    }

    @Override
    public ItemTemplate getTemplate() {
        return stats;
    }

    public boolean isReusable() {
        return stats.isReusable();
    }

    public void onLoad(ItemWeapon weapon) {
        for (Effect effect : stats.getWeaponEffects()) {
            weapon.addEffect(effect);
        }
    }

    public void onUnload(ItemWeapon weapon) {
        for (Effect effect : stats.getWeaponEffects()) {
            weapon.removeEffect(effect);
        }
    }

}
