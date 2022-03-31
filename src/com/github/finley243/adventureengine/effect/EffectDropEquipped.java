package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemEquippable;

public class EffectDropEquipped extends Effect {

    public EffectDropEquipped() {
        super(0, false, 0);
    }

    @Override
    public void start(Actor target) {
        ItemEquippable item = target.equipmentComponent().getEquippedItem();
        if(item == null) return;
        target.inventory().removeItem(item);
        target.getArea().addObject(item);
        item.setArea(target.getArea());
        Context context = new Context(target, item);
        target.game().eventBus().post(new AudioVisualEvent(target.getArea(), Phrases.get("forceDrop"), context, null, target));
    }

    @Override
    public void end(Actor target) {

    }

    @Override
    public void eachTurn(Actor target) {

    }

}
