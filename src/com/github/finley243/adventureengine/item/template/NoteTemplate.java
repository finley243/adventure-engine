package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NoteTemplate extends ItemTemplate {

    private final List<String> text;

    public NoteTemplate(String ID, String name, Scene description, Map<String, Script> scripts, int price, List<String> text) {
        super(ID, name, description, scripts, price);
        this.text = text;
    }

    @Override
    public boolean hasState() {
        return false;
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
