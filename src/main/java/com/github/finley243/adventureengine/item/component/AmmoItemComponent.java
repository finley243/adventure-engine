package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.AmmoItemComponentTemplate;

public class AmmoItemComponent extends ItemComponent {

    public AmmoItemComponent(Item item, ItemComponentTemplate template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private AmmoItemComponentTemplate getAmmoTemplate() {
        return (AmmoItemComponentTemplate) getTemplate();
    }

    public boolean isReusable() {
        return getAmmoTemplate().isReusable();
    }

    public void onLoad(Item weapon) {
        for (Effect effect : getAmmoTemplate().getWeaponEffects()) {
            weapon.getComponentOfType(EffectableItemComponent.class).addEffect(effect);
        }
    }

    public void onUnload(Item weapon) {
        for (Effect effect : getAmmoTemplate().getWeaponEffects()) {
            weapon.getComponentOfType(EffectableItemComponent.class).removeEffect(effect);
        }
    }

}
