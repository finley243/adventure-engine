package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.component.EffectComponent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;

public class ItemComponentEffectible extends ItemComponent {

    private final EffectComponent effectComponent;

    public ItemComponentEffectible(Item item, ItemComponentTemplate template) {
        super(item, template);
        this.effectComponent = new EffectComponent(item.game(), item, Context.builder(item.game()).subject(item.game().data().getPlayer()).target(item.game().data().getPlayer()).parentItem(item).build());
    }

    @Override
    public boolean hasState() {
        return effectComponent.hasAnyEffect();
    }

    public void addEffect(String effectID) {
        effectComponent.addEffect(effectID);
    }

    public void removeEffect(String effectID) {
        effectComponent.removeEffect(effectID);
    }

    @Override
    public void onStartRound() {
        effectComponent.onStartRound();
    }

}
