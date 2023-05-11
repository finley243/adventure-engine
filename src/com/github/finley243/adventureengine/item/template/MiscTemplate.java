package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MiscTemplate extends ItemTemplate {

    public MiscTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, int price) {
        super(game, ID, name, description, scripts, price);
    }

    @Override
    public boolean hasState() {
        return false;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("misc");
        return tags;
    }

}
