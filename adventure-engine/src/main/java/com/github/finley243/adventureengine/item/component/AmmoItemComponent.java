package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.AmmoItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;

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

}
