package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.world.Physical;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ActionGeneric extends Action{

    public enum ActionMatchType {
        NONE, OBJECT, ACTION
    }

    private final Physical object;
    private final String action;
    private final float utility;
    private final boolean usesAction;
    private final boolean canRepeat;
    private final ActionMatchType matchType;
    private final Set<String> matchActions;
    private final int actionCount;
    private final MenuData menuData;

    public ActionGeneric(Physical object, String action, float utility, boolean usesAction, boolean canRepeat, ActionMatchType matchType, int actionCount, MenuData menuData, String... otherMatches) {
        this.object = object;
        this.action = action;
        this.utility = utility;
        this.usesAction = usesAction;
        this.canRepeat = canRepeat;
        this.matchType = matchType;
        this.actionCount = actionCount;
        this.menuData = menuData;
        this.matchActions = new HashSet<>();
        matchActions.add(this.action);
        if(otherMatches != null) {
            matchActions.addAll(Arrays.asList(otherMatches));
        }
    }

    @Override
    public void choose(Actor subject) {
        object.executeAction(action, subject);
    }

    @Override
    public float utility(Actor subject) {
        return utility;
    }

    @Override
    public boolean usesAction() {
        return usesAction;
    }

    @Override
    public boolean canRepeat() {
        return canRepeat;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        switch(matchType) {
            case OBJECT:
                return (action instanceof ActionGeneric) && this.action.equals(((ActionGeneric) action).action) && this.object.equals(((ActionGeneric) action).object);
            case ACTION:
                return (action instanceof ActionGeneric) && this.matchActions.contains(((ActionGeneric) action).action);
            case NONE:
            default:
                return false;
        }
    }

    @Override
    public int actionCount() {
        return actionCount;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return menuData;
    }

}
