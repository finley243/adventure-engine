package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.RuntimeStack;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptReturnTarget;
import com.github.finley243.adventureengine.script.ScriptStat;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class DamageActorEvent implements QueuedEvent, ScriptReturnTarget {

    private final Actor actor;
    private final Damage damageData;
    private final Context damageContext;

    private Integer equipmentResistance;
    private Float equipmentMult;
    private Integer actorResistance;
    private Float actorMult;

    public DamageActorEvent(Actor actor, Damage damageData, Context damageContext) {
        this.actor = actor;
        this.damageData = damageData;
        this.damageContext = damageContext;
    }

    @Override
    public void execute(Game game) {
        RuntimeStack runtimeStack = new RuntimeStack();
        runtimeStack.addContext(damageContext, this);
        computeNextValue(runtimeStack);
    }

    public void computeNextValue(RuntimeStack runtimeStack) {
        Script script;
        if (equipmentResistance == null) {
            if (damageData.getLimb() != null) {
                script = Script.constant(actor.getEquipmentComponent().getDamageResistanceLimb(damageData.getLimb().getID(), damageData.getType()));
            } else {
                script = Script.constant(actor.getEquipmentComponent().getDamageResistanceMain(damageData.getType()));
            }
        } else if (equipmentMult == null) {
            if (damageData.getLimb() != null) {
                script = Script.constant(actor.getEquipmentComponent().getDamageMultLimb(damageData.getLimb().getID(), damageData.getType()));
            } else {
                script = Script.constant(actor.getEquipmentComponent().getDamageMultMain(damageData.getType()));
            }
        } else if (actorResistance == null) {
            script = new ScriptStat(new StatHolderReference("target", null, null), Expression.constant("damage_resist_" + damageData.getType()));
        } else {
            script = new ScriptStat(new StatHolderReference("target", null, null), Expression.constant("damage_mult_" + damageData.getType()));
        }
        script.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, Script.ScriptReturnData scriptReturnData) {
        if (scriptReturnData.error() != null) {
            throw new IllegalArgumentException("Script threw an error");
        } else if (scriptReturnData.isReturn()) {
            throw new IllegalArgumentException("Script contained a return statement (this should never occur)");
        } else if (scriptReturnData.value() == null) {
            throw new IllegalArgumentException("Script provided a null value");
        } else if (equipmentResistance == null) {
            if (scriptReturnData.value().getDataType(runtimeStack.getContext()) != Expression.DataType.INTEGER) throw new IllegalArgumentException("Equipment resistance value is not an integer");
            equipmentResistance = scriptReturnData.value().getValueInteger(runtimeStack.getContext());
            computeNextValue(runtimeStack);
        } else if (equipmentMult == null) {
            if (scriptReturnData.value().getDataType(runtimeStack.getContext()) != Expression.DataType.FLOAT) throw new IllegalArgumentException("Equipment mult value is not a float");
            equipmentMult = scriptReturnData.value().getValueFloat(runtimeStack.getContext());
            computeNextValue(runtimeStack);
        } else if (actorResistance == null) {
            if (scriptReturnData.value().getDataType(runtimeStack.getContext()) != Expression.DataType.INTEGER) throw new IllegalArgumentException("Actor resistance value is not an integer");
            actorResistance = scriptReturnData.value().getValueInteger(runtimeStack.getContext());
            computeNextValue(runtimeStack);
        } else if (actorMult == null) {
            if (scriptReturnData.value().getDataType(runtimeStack.getContext()) != Expression.DataType.FLOAT) throw new IllegalArgumentException("Actor mult value is not a float");
            actorMult = scriptReturnData.value().getValueFloat(runtimeStack.getContext());
            damageDirect();
        } else {
            throw new IllegalArgumentException("No further script values expected");
        }
    }

    @Override
    public boolean continueAfterExecution() {
        return false;
    }

    private void damageDirect() {
        for (String effectID : damageData.getTargetEffects()) {
            actor.getEffectComponent().addEffect(effectID);
        }
        int amount = damageData.getAmount();
        // TODO - Add additional armor mult for damage mults (part of the Damage object, affected by weapons/attacks/etc.)
        amount -= Math.round(equipmentResistance * damageData.getArmorMult());
        amount -= Math.round(amount * equipmentMult);
        amount -= Math.round(actorResistance * damageData.getArmorMult());
        amount -= Math.round(amount * actorMult);
        if (damageData.getLimb() != null) {
            amount = Math.round(amount * damageData.getLimb().getDamageMult());
        }
        if (amount < 0) amount = 0;
        if (damageData.getLimb() != null && amount > 0) {
            damageData.getLimb().applyEffects(actor);
        }
        actor.modifyHP(-amount, damageContext);
        actor.game().eventQueue().startExecution();
    }

}
