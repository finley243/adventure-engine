package com.github.finley243.adventureengine.world.item.template;

import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NoteTemplate extends ItemTemplate {

    private final List<String> text;

    public NoteTemplate(String ID, String name, String description, Map<String, Script> scripts, int price, List<String> text) {
        super(ID, name, description, scripts, price);
        this.text = text;
    }

    public List<String> getText() {
        return text;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("note");
        return tags;
    }

}
