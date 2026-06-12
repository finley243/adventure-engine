package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class ObjectLoader {

    private final ScriptParser scriptParser;
    private final Registry<ObjectTemplate> objectTemplateRegistry;

    public ObjectLoader(ScriptParser scriptParser, Registry<ObjectTemplate> objectTemplateRegistry) {
        this.scriptParser = scriptParser;
        this.objectTemplateRegistry = objectTemplateRegistry;
    }

    WorldObject parseObject(Element element) {
        String templateID = LoadUtils.attribute(element, "template", null);
        ObjectTemplate template = objectTemplateRegistry.getFromID(templateID);
        if (template == null) return null;
        String id = LoadUtils.attribute(element, "id", null);
        boolean startDisabled = LoadUtils.attributeBool(element, "startDisabled", false);
        boolean startHidden = LoadUtils.attributeBool(element, "startHidden", false);
        Map<String, Expression> localVarsDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(element, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            Expression varExpression = LoadUtils.loadScriptLiteral(varDefaultElement, scriptParser, "Object(" + id + ") - local var: " + varName);
            localVarsDefault.put(varName, varExpression);
        }
        return new WorldObject(id, template, startDisabled, startHidden, localVarsDefault);
    }

}
