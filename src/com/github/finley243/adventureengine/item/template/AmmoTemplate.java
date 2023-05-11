package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmmoTemplate extends ItemTemplate {

    private final List<String> weaponEffects;
    private final boolean isReusable;

    public AmmoTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, int price, List<String> weaponEffects, boolean isReusable) {
        super(game, ID, name, description, scripts, price);
        this.weaponEffects = weaponEffects;
        this.isReusable = isReusable;
    }

    @Override
    public boolean hasState() {
        return false;
    }

    public List<String> getWeaponEffects() {
        return weaponEffects;
    }

    public boolean isReusable() {
        return isReusable;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("ammo");
        return tags;
    }

}
