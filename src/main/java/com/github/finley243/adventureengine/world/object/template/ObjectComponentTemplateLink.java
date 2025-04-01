package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.condition.Condition;

import java.util.Map;

public class ObjectComponentTemplateLink extends ObjectComponentTemplate {

    private final Map<String, ObjectLinkData> linkData;

    public ObjectComponentTemplateLink(boolean startEnabled, boolean actionsRestricted, Map<String, ObjectLinkData> linkData) {
        super(startEnabled, actionsRestricted);
        this.linkData = linkData;
    }

    public Map<String, ObjectLinkData> getLinkData() {
        return linkData;
    }

    public record ObjectLinkData(String moveAction, Condition conditionVisible, boolean isVisible) {}

}
