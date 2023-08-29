package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;
import java.util.Map;

public class ObjectTemplate extends GameInstanced {

    private final String name;
    private final boolean isProperName;
    private final Scene description;
    private final int maxHP;
    private final Map<String, Integer> damageResistances;
    private final Map<String, Float> damageMults;
    private final Map<String, Script> scripts;
    private final List<ActionCustom.CustomActionHolder> customActions;
    private final List<ActionCustom.CustomActionHolder> networkActions;
    private final List<ObjectComponentTemplate> components;
    private final Map<String, Expression> localVarsDefault;

    public ObjectTemplate(Game game, String ID, String name, boolean isProperName, Scene description, int maxHP, Map<String, Integer> damageResistances, Map<String, Float> damageMults, Map<String, Script> scripts, List<ActionCustom.CustomActionHolder> customActions, List<ActionCustom.CustomActionHolder> networkActions, List<ObjectComponentTemplate> components, Map<String, Expression> localVarsDefault) {
        super(game, ID);
        this.name = name;
        this.isProperName = isProperName;
        this.description = description;
        this.maxHP = maxHP;
        this.damageResistances = damageResistances;
        this.damageMults = damageMults;
        this.scripts = scripts;
        this.customActions = customActions;
        this.networkActions = networkActions;
        this.components = components;
        this.localVarsDefault = localVarsDefault;
    }

    public String getName() {
        return name;
    }

    public boolean isProperName() {
        return isProperName;
    }

    public Scene getDescription() {
        return description;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getDamageResistance(String damageType) {
        return damageResistances.getOrDefault(damageType, 0);
    }

    public float getDamageMult(String damageType) {
        return damageMults.getOrDefault(damageType, 0.0f);
    }

    public Map<String, Script> getScripts() {
        return scripts;
    }

    public List<ActionCustom.CustomActionHolder> getCustomActions() {
        return customActions;
    }

    public List<ActionCustom.CustomActionHolder> getNetworkActions() {
        return networkActions;
    }

    public List<ObjectComponentTemplate> getComponents() {
        return components;
    }

    public Map<String, Expression> getLocalVarsDefault() {
        return localVarsDefault;
    }

}
