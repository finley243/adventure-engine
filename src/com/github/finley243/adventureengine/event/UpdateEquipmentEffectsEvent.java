package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.script.RuntimeStack;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptReturnTarget;

import java.util.Set;

public class UpdateEquipmentEffectsEvent implements QueuedEvent, ScriptReturnTarget {

    private final boolean addEffects;
    private final Actor actor;
    private final Item item;

    public UpdateEquipmentEffectsEvent(boolean addEffects, Actor actor, Item item) {
        this.addEffects = addEffects;
        this.actor = actor;
        this.item = item;
    }

    @Override
    public void execute(Game game) {
        RuntimeStack runtimeStack = new RuntimeStack();
        Context context = new Context(game, actor, actor, item);
        runtimeStack.addContext(context, this);
        Script script = actor.getStatValue("equipment_effects", context);
        script.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, Script.ScriptReturnData scriptReturnData) {
        Expression equipmentEffectsExpression = scriptReturnData.value();
        if (equipmentEffectsExpression == null) throw new IllegalArgumentException("Equipment effects stat is null (this should never occur)");
        Set<String> equipmentEffects = equipmentEffectsExpression.getValueStringSet(runtimeStack.getContext());
        // TODO - Expand to all equippable items (not just weapons)
        if (addEffects) {
            if (item instanceof ItemWeapon weapon) {
                for (String equipmentEffect : equipmentEffects) {
                    weapon.getEffectComponent().addEffect(equipmentEffect);
                }
            }
        } else {
            if (item instanceof ItemWeapon weapon) {
                for (String equipmentEffect : equipmentEffects) {
                    weapon.getEffectComponent().removeEffect(equipmentEffect);
                }
            }
        }
        runtimeStack.getContext().game().eventQueue().startExecution();
    }

    @Override
    public boolean continueAfterExecution() {
        return false;
    }

}
