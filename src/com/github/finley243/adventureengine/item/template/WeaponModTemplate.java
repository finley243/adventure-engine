package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeaponModTemplate extends ItemTemplate {

    private final String modSlot;
    private final List<String> weaponEffects;

    public WeaponModTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, List<ActionCustom.CustomActionHolder> customActions, int price, String modSlot, List<String> weaponEffects) {
        super(game, ID, name, description, scripts, customActions, price);
        this.modSlot = modSlot;
        this.weaponEffects = weaponEffects;
    }

    @Override
    public boolean hasState() {
        return false;
    }

    @Override
    public Set<String> getTags() {
        return null;
    }

    public String getModSlot() {
        return modSlot;
    }

    public List<String> getWeaponEffects() {
        return weaponEffects;
    }

}
