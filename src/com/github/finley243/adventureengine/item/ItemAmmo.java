package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.effect.AreaEffect;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.template.AmmoTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

import java.util.List;

public class ItemAmmo extends Item {

    private final AmmoTemplate stats;

    public ItemAmmo(Game game, String ID, AmmoTemplate stats) {
        super(game, ID);
        this.stats = stats;
    }

    @Override
    public ItemTemplate getTemplate() {
        return stats;
    }

    public boolean isReusable() {
        return stats.isReusable();
    }

    public int getDamage() {
        return stats.getDamage();
    }

    public Damage.DamageType getDamageType() {
        return stats.getDamageType();
    }

    public float getArmorMult() {
        return stats.getArmorMult();
    }

    public List<Effect> getTargetEffects() {
        return stats.getTargetEffects();
    }

    public AreaEffect getAreaEffect() {
        return stats.getAreaEffect();
    }

    public void onLoad(ItemWeapon weapon) {
        for (Effect effect : stats.getWeaponEffects()) {
            weapon.addEffect(effect);
        }
    }

    public void onUnload(ItemWeapon weapon) {
        for (Effect effect : stats.getWeaponEffects()) {
            weapon.removeEffect(effect);
        }
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        if (stats.isThrowable()) {
            // TODO - Add throwing actions
            if (stats.isAreaTargeted()) {

            } else {

            }
        }
        return actions;
    }

}
