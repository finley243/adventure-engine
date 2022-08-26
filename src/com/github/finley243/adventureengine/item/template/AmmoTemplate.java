package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmmoTemplate extends ItemTemplate {

    private final List<Effect> effects;

    public AmmoTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, List<Effect> effects) {
        super(ID, name, description, scripts, price);
        this.effects = effects;
    }

    @Override
    public boolean hasState() {
        return false;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("ammo");
        return tags;
    }

}
