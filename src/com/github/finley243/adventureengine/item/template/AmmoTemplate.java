package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AmmoTemplate extends ItemTemplate {

    public AmmoTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price) {
        super(ID, name, description, scripts, price);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("ammo");
        return tags;
    }

}
