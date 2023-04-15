package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.variable.Variable;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.HashMap;
import java.util.Map;

public class ActionMoveCustomLink extends ActionCustom {

    private final String template;
    private final AreaLink areaLink;
    private final Map<String, Variable> parameters;

    public ActionMoveCustomLink(Game game, String template, AreaLink areaLink, Map<String, Variable> parameters) {
        super(game, null, template, parameters);
        this.template = template;
        this.areaLink = areaLink;
        this.parameters = parameters;
    }

    @Override
    protected MapBuilder<String, Noun> getContextNounMap(Actor subject) {
        Noun moveLocation;
        if (!getDestinationArea().getRoom().equals(subject.getArea().getRoom())) {
            moveLocation = getDestinationArea().getRoom();
        } else {
            moveLocation = getDestinationArea();
        }
        return super.getContextNounMap(subject).put("area", moveLocation);
    }

    @Override
    public void onSuccess(Actor subject, Context context) {
        super.onSuccess(subject, context);
        Area lastArea = subject.getArea();
        subject.setArea(game().data().getArea(areaLink.getAreaID()));
        subject.onMove(lastArea);
    }

    @Override
    public void onFailure(Actor subject, Context context) {
        super.onFailure(subject, context);
    }

    @Override
    public int repeatCount(Actor subject) {
        return Actor.MOVES_PER_TURN;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionMoveCustomLink;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        return action instanceof ActionMoveCustomLink;
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && subject.canMove();
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        Map<String, String> contextVars = new HashMap<>();
        for (Map.Entry<String, Variable> entry : getTemplate().getTextVars().entrySet()) {
            contextVars.put(entry.getKey(), entry.getValue().getValueString(new ContextScript(subject.game(), subject, subject, null, parameters)));
        }
        String promptWithVars = TextGen.generateVarsOnly(getTemplate().getPrompt(), contextVars);
        return new MenuChoice("(" + areaLink.getDirection() + ") " + LangUtils.titleCase(promptWithVars), canChoose(subject), new String[]{"move"}, new String[]{});
    }

    public Area getDestinationArea() {
        return game().data().getArea(areaLink.getAreaID());
    }

    @Override
    public ActionDetectionChance detectionChance() {
        return ActionDetectionChance.HIGH;
    }

}
