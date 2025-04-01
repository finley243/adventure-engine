package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.action.ActionCustom;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectComponentTemplateUsable extends ObjectComponentTemplate {

    private Map<String, UsableSlotData> usableSlotData;

    public ObjectComponentTemplateUsable(boolean startEnabled, boolean actionsRestricted, Map<String, UsableSlotData> usableSlotData) {
        super(startEnabled, actionsRestricted);
        this.usableSlotData = usableSlotData;
    }

    public Map<String, UsableSlotData> getUsableSlotData() {
        return usableSlotData;
    }

    public record UsableSlotData(String startPhrase, String endPhrase, String startPrompt, String endPrompt, boolean userIsInCover, boolean userIsHidden, boolean userCanSeeOtherAreas, boolean userCanPerformLocalActions, boolean userCanPerformParentActions, Set<String> componentsExposed, List<ActionCustom.CustomActionHolder> usingActions) {}

}
