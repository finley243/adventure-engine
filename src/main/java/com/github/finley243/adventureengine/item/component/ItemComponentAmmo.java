package com.github.finley243.adventureengine.item.component;

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

    public void onLoad(Item weapon) {
        for (String effectID : getAmmoTemplate().getWeaponEffects()) {
            weapon.getComponentOfType(ItemComponentEffectible.class).addEffect(effectID);
        }
    }

    public void onUnload(Item weapon) {
        for (String effectID : getAmmoTemplate().getWeaponEffects()) {
            weapon.getComponentOfType(ItemComponentEffectible.class).removeEffect(effectID);
        }
    }

}
