package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class ObjectLoader {

    private final ScriptParser scriptParser;

    public ObjectLoader(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    WorldObject parseObject(Element element, Area area) {
        if (element == null) return null;
        String template = LoadUtils.attribute(element, "template", null);
        String id = LoadUtils.attribute(element, "id", null);
        boolean startDisabled = LoadUtils.attributeBool(element, "startDisabled", false);
        boolean startHidden = LoadUtils.attributeBool(element, "startHidden", false);
        Map<String, Expression> localVarsDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(element, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            Expression varExpression = LoadUtils.loadScriptLiteral(varDefaultElement, scriptParser, "Object(" + id + ") - local var: " + varName);
            localVarsDefault.put(varName, varExpression);
        }
        return new WorldObject(id, template, area, startDisabled, startHidden, localVarsDefault);
    }

}
