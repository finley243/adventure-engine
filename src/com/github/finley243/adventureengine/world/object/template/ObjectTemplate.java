package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;
import java.util.Map;

public class ObjectTemplate {

    private final String ID;
    private final String name;
    private final Scene description;
    private final Map<String, Script> scripts;
    private final List<ActionCustom> customActions;
    private final Map<String, String> components;

    public ObjectTemplate(String ID, String name, Scene description, Map<String, Script> scripts, List<ActionCustom> customActions, Map<String, String> components) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.scripts = scripts;
        this.customActions = customActions;
        this.components = components;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Scene getDescription() {
        return description;
    }

    public Map<String, Script> getScripts() {
        return scripts;
    }

    public List<ActionCustom> getCustomActions() {
        return customActions;
    }

    public Map<String, String> getComponents() {
        return components;
    }

}
