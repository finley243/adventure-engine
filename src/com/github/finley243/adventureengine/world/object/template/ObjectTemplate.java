package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectTemplate extends GameInstanced {

    private final String name;
    private final Scene description;
    private final Map<String, Script> scripts;
    private final List<CustomActionHolder> customActions;
    private final Map<String, String> components;
    private final Map<String, Boolean> localVarsBooleanDefault;
    private final Map<String, Integer> localVarsIntegerDefault;
    private final Map<String, Float> localVarsFloatDefault;
    private final Map<String, String> localVarsStringDefault;
    private final Map<String, Set<String>> localVarsStringSetDefault;

    public ObjectTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, List<CustomActionHolder> customActions, Map<String, String> components, Map<String, Boolean> localVarsBooleanDefault, Map<String, Integer> localVarsIntegerDefault, Map<String, Float> localVarsFloatDefault, Map<String, String> localVarsStringDefault, Map<String, Set<String>> localVarsStringSetDefault) {
        super(game, ID);
        this.name = name;
        this.description = description;
        this.scripts = scripts;
        this.customActions = customActions;
        this.components = components;
        this.localVarsBooleanDefault = localVarsBooleanDefault;
        this.localVarsIntegerDefault = localVarsIntegerDefault;
        this.localVarsFloatDefault = localVarsFloatDefault;
        this.localVarsStringDefault = localVarsStringDefault;
        this.localVarsStringSetDefault = localVarsStringSetDefault;
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

    public List<CustomActionHolder> getCustomActions() {
        return customActions;
    }

    public Map<String, String> getComponents() {
        return components;
    }

    public Map<String, Boolean> getLocalVarsBooleanDefault() {
        return localVarsBooleanDefault;
    }

    public Map<String, Integer> getLocalVarsIntegerDefault() {
        return localVarsIntegerDefault;
    }

    public Map<String, Float> getLocalVarsFloatDefault() {
        return localVarsFloatDefault;
    }

    public Map<String, String> getLocalVarsStringDefault() {
        return localVarsStringDefault;
    }

    public Map<String, Set<String>> getLocalVarsStringSetDefault() {
        return localVarsStringSetDefault;
    }

    public record CustomActionHolder(String action, Map<String, Variable> parameters) {}

}
