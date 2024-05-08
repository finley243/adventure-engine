package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateArmor;

import java.util.Set;

public class ItemComponentArmor extends ItemComponent {

    public ItemComponentArmor(Item item, ItemComponentTemplate template) {
        super(item, template);
    }

    private ItemComponentTemplateArmor getArmorTemplate() {
        return (ItemComponentTemplateArmor) getTemplate();
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
