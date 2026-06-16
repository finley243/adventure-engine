package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.LinkObjectComponent;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentFactory;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectLoader {

    private final ScriptParser scriptParser;
    private final ObjectComponentFactory objectComponentFactory;
    private final Registry<ObjectTemplate> objectTemplateRegistry;

    public ObjectLoader(ScriptParser scriptParser, ObjectComponentFactory objectComponentFactory, Registry<ObjectTemplate> objectTemplateRegistry) {
        this.scriptParser = scriptParser;
        this.objectComponentFactory = objectComponentFactory;
        this.objectTemplateRegistry = objectTemplateRegistry;
    }

    WorldObject parseObject(Element element) {
        String templateID = LoadUtils.attribute(element, "template", null);
        ObjectTemplate template = objectTemplateRegistry.getFromID(templateID);
        if (template == null) return null;
        String id = LoadUtils.attribute(element, "id", null);
        boolean startDisabled = LoadUtils.attributeBool(element, "startDisabled", false);
        boolean startHidden = LoadUtils.attributeBool(element, "startHidden", false);
        Set<LinkObjectComponent.LinkDataIntermediate> objectLinks = new HashSet<>();
        for (Element objectLinkElement : LoadUtils.directChildrenWithName(element, "objectLink")) {
            String linkID = LoadUtils.attribute(objectLinkElement, "link", null);
            String objectID = LoadUtils.attribute(objectLinkElement, "object", null);
            AreaLink.CompassDirection direction = LoadUtils.attributeEnum(objectLinkElement, "dir", AreaLink.CompassDirection.class, null);
            objectLinks.add(new LinkObjectComponent.LinkDataIntermediate(linkID, objectID, direction));
        }
        String vehicleOverrideObjectID = LoadUtils.singleTag(element, "vehicleObjectOverride", null);
        String networkNodeID = LoadUtils.singleTag(element, "networkID", null);
        Map<String, Expression> localVarsDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(element, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            Expression varExpression = LoadUtils.loadScriptLiteral(varDefaultElement, scriptParser, "Object(" + id + ") - local var: " + varName);
            localVarsDefault.put(varName, varExpression);
        }
        return new WorldObject(id, template, startDisabled, startHidden, objectLinks, vehicleOverrideObjectID, networkNodeID, localVarsDefault, objectComponentFactory);
    }

}
