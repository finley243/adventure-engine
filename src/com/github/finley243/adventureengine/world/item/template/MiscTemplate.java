package com.github.finley243.adventureengine.world.item.template;

import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MiscTemplate extends ItemTemplate {

    public MiscTemplate(String ID, String name, String description, Map<String, Script> scripts, int price) {
        super(ID, name, description, scripts, price);
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("misc");
        return tags;
    }

}
