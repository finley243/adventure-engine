package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

import java.util.List;
import java.util.Set;

public class ObjectComponentTemplateUsable extends ObjectComponentTemplate {

    private final String startPhrase;
    private final String endPhrase;
    private final String startPrompt;
    private final String endPrompt;
    private final boolean userIsInCover;
    private final boolean userIsHidden;
    private final boolean userCanSeeOtherAreas;
    // Actions from components specified by local IDs will be added to usable actions
    private final Set<String> componentsExposed;
    private final List<ObjectTemplate.CustomActionHolder> usingActions;

    public ObjectComponentTemplateUsable(Game game, String ID, boolean startEnabled, boolean actionsRestricted, String name, String startPhrase, String endPhrase, String startPrompt, String endPrompt, boolean userIsInCover, boolean userIsHidden, boolean userCanSeeOtherAreas, Set<String> componentsExposed, List<ObjectTemplate.CustomActionHolder> usingActions) {
        super(game, ID, startEnabled, actionsRestricted, name);
        this.startPhrase = startPhrase;
        this.endPhrase = endPhrase;
        this.startPrompt = startPrompt;
        this.endPrompt = endPrompt;
        this.userIsInCover = userIsInCover;
        this.userIsHidden = userIsHidden;
        this.userCanSeeOtherAreas = userCanSeeOtherAreas;
        this.componentsExposed = componentsExposed;
        this.usingActions = usingActions;
    }

    public String getStartPhrase() {
        return startPhrase;
    }

    public String getEndPhrase() {
        return endPhrase;
    }

    public String getStartPrompt() {
        return startPrompt;
    }

    public String getEndPrompt() {
        return endPrompt;
    }

    public boolean userIsInCover() {
        return userIsInCover;
    }

    public boolean userIsHidden() {
        return userIsHidden;
    }

    public boolean userCanSeeOtherAreas() {
        return userCanSeeOtherAreas;
    }

    public Set<String> getComponentsExposed() {
        return componentsExposed;
    }

    public List<ObjectTemplate.CustomActionHolder> getUsingActions() {
        return usingActions;
    }

}
