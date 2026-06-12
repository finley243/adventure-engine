package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateAmmo;

public class ItemComponentAmmo extends ItemComponent {

    public ItemComponentAmmo(Item item, ItemComponentTemplate template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private ItemComponentTemplateAmmo getAmmoTemplate() {
        return (ItemComponentTemplateAmmo) getTemplate();
    }

    public boolean isReusable() {
        return getAmmoTemplate().isReusable();
    }

    public void onLoad(Game game, Item weapon) {
        for (String effectID : getAmmoTemplate().getWeaponEffects()) {
            Effect effect = game.data().getEffect(effectID);
            weapon.getComponentOfType(ItemComponentEffectible.class).addEffect(effect);
        }
    }

    public void onUnload(Game game, Item weapon) {
        for (String effectID : getAmmoTemplate().getWeaponEffects()) {
            Effect effect = game.data().getEffect(effectID);
            weapon.getComponentOfType(ItemComponentEffectible.class).removeEffect(effect);
        }
    }

}
