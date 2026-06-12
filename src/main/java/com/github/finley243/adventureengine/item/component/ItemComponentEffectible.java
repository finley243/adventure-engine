package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.component.EffectComponent;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public class ItemComponentEffectible extends ItemComponent {

    private final EffectComponent effectComponent;

    public ItemComponentEffectible(Item item, ItemComponentTemplate template, ScriptRuntime scriptRuntime, Context scriptContext) {
        super(item, template);
        this.effectComponent = new EffectComponent(getItem(), scriptRuntime, scriptContext);
    }

    @Override
    public boolean hasState() {
        return effectComponent.hasAnyEffect();
    }

    public void addEffect(Effect effect) {
        effectComponent.addEffect(effect);
    }

    public void removeEffect(Effect effect) {
        effectComponent.removeEffect(effect);
    }

    @Override
    public void onStartRound() {
        effectComponent.onStartRound();
    }

}
