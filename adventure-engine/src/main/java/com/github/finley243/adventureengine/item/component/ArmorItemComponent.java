package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ArmorItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;

import java.util.Set;

public class ArmorItemComponent extends ItemComponent {

    public ArmorItemComponent(Item item, ItemComponentTemplate template) {
        super(item, template);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    private ArmorItemComponentTemplate getArmorTemplate() {
        return (ArmorItemComponentTemplate) getTemplate();
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
