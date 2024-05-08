package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.component.EffectComponent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;

public class ItemComponentEffectable extends ItemComponent {

    // TODO - EffectComponent round updates (handled by current inventory owner, either actor or area)
    private final EffectComponent effectComponent;

    public ItemComponentEffectable(Item item, ItemComponentTemplate template) {
        super(item, template);
        this.effectComponent = new EffectComponent(item.game(), item, new Context(item.game(), item.game().data().getPlayer(), item.game().data().getPlayer(), item));
    }

    public void addEffect(String effectID) {
        effectComponent.addEffect(effectID);
    }

    public void removeEffect(String effectID) {
        effectComponent.removeEffect(effectID);
    }

}
