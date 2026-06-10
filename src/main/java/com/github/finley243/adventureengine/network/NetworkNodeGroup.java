package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkNodeGroup extends NetworkNode {

    private final Set<NetworkNode> childNodes;

    public NetworkNodeGroup(String ID, String name, Set<NetworkNode> childNodes) {
        super(ID, name);
        this.childNodes = childNodes;
    }

    @Override
    protected List<Action> breachedActions(Game game, Actor subject, WorldObject object) {
        List<Action> actions = new ArrayList<>();
        for (NetworkNode childNode : childNodes) {
            actions.addAll(childNode.actions(game, subject, object));
        }
        return actions;
    }

    @Override
    public Expression getStatValue(String name, Context context, Game game) {
        return switch (name) {
            case "childNodes" -> Expression.constant(getAllChildNodes());
            case "directChildNodes" -> Expression.constant(childNodes);
            case "childObjects" -> Expression.constant(getAllChildObjects());
            case "directChildObjects" -> Expression.constant(getDirectChildObjects());
            default -> super.getStatValue(name, context, game);
        };
    }

    private Set<NetworkNode> getAllChildNodes() {
        Set<NetworkNode> children = new HashSet<>(childNodes);
        for (NetworkNode node : childNodes) {
            if (node instanceof NetworkNodeGroup groupNode) {
                children.addAll(groupNode.getAllChildNodes());
            }
        }
        return children;
    }

    private Set<WorldObject> getDirectChildObjects() {
        Set<WorldObject> childObjects = new HashSet<>();
        for (NetworkNode node : childNodes) {
            if (node instanceof NetworkNodeControl controlNode) {
                childObjects.add(controlNode.getObject());
            }
        }
        return childObjects;
    }

    private Set<WorldObject> getAllChildObjects() {
        Set<WorldObject> childObjects = new HashSet<>();
        for (NetworkNode node : childNodes) {
            if (node instanceof NetworkNodeControl controlNode) {
                childObjects.add(controlNode.getObject());
            } else if (node instanceof NetworkNodeGroup groupNode) {
                childObjects.addAll(groupNode.getAllChildObjects());
            }
        }
        return childObjects;
    }

}
