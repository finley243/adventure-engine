package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModTemplate extends ItemTemplate {

    private final String modSlot;
    private final List<String> effects;

    public ModTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, List<ActionCustom.CustomActionHolder> customActions, int price, String modSlot, List<String> effects) {
        super(game, ID, name, description, scripts, customActions, price);
        this.modSlot = modSlot;
        this.effects = effects;
    }

    @Override
    public Set<String> getTags() {
        Set<String> tags = new HashSet<>();
        tags.add("mod");
        tags.add("mod_slot_" + modSlot);
        return tags;
    }

    public String getModSlot() {
        return modSlot;
    }

    public List<String> getEffects() {
        return effects;
    }

}
