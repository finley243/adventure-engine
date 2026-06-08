package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.component.EffectComponent;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;

public class ItemComponentEffectible extends ItemComponent {

    private EffectComponent effectComponent;

    public ItemComponentEffectible(Item item, ItemComponentTemplate template) {
        super(item, template);
    }

    @Override
    public void onInit(Game game) {
        super.onInit(game);
        this.effectComponent = new EffectComponent(getItem(), Context.builder(game).subject(game.data().getPlayer()).target(game.data().getPlayer()).parentItem(getItem()).build());
    }

    @Override
    public boolean hasState() {
        if (effectComponent == null) throw new  IllegalStateException("ItemComponentEffectible has not been initialized");
        return effectComponent.hasAnyEffect();
    }

    public void addEffect(Game game, Effect effect) {
        if (effectComponent == null) throw new  IllegalStateException("ItemComponentEffectible has not been initialized");
        effectComponent.addEffect(game, effect);
    }

    public void removeEffect(Game game, Effect effect) {
        if (effectComponent == null) throw new  IllegalStateException("ItemComponentEffectible has not been initialized");
        effectComponent.removeEffect(game, effect);
    }

    @Override
    public void onStartRound(Game game) {
        if (effectComponent == null) throw new  IllegalStateException("ItemComponentEffectible has not been initialized");
        effectComponent.onStartRound(game);
    }

}
