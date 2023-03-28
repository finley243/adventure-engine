package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;

import java.util.List;

public class ObjectComponentTemplateUsable extends ObjectComponentTemplate {

    private final String startPhrase;
    private final String endPhrase;
    private final String startPrompt;
    private final String endPrompt;
    private final boolean userIsInCover;
    private final boolean userIsHidden;
    private final boolean userCanSeeOtherAreas;
    private final List<ObjectTemplate.CustomActionHolder> usingActions;

    public ObjectComponentTemplateUsable(Game game, String ID, boolean startEnabled, String name, String startPhrase, String endPhrase, String startPrompt, String endPrompt, boolean userIsInCover, boolean userIsHidden, boolean userCanSeeOtherAreas, List<ObjectTemplate.CustomActionHolder> usingActions) {
        super(game, ID, startEnabled, name);
        this.startPhrase = startPhrase;
        this.endPhrase = endPhrase;
        this.startPrompt = startPrompt;
        this.endPrompt = endPrompt;
        this.userIsInCover = userIsInCover;
        this.userIsHidden = userIsHidden;
        this.userCanSeeOtherAreas = userCanSeeOtherAreas;
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

    public List<ObjectTemplate.CustomActionHolder> getUsingActions() {
        return usingActions;
    }

}
