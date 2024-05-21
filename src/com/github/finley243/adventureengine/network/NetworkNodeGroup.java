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

    public NetworkNodeGroup(Game game, String ID, String templateID, String name, Set<NetworkNode> childNodes) {
        super(game, ID, templateID, name);
        this.childNodes = childNodes;
    }

    @Override
    protected List<Action> breachedActions(Actor subject, WorldObject object) {
        List<Action> actions = new ArrayList<>();
        for (NetworkNode childNode : childNodes) {
            actions.addAll(childNode.actions(subject, object));
        }
        return actions;
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "childNodes" -> Expression.constant(getAllChildNodes());
            case "directChildNodes" -> Expression.constant(childNodes);
            case "childObjects" -> Expression.constant(getAllChildObjects(context.game()));
            case "directChildObjects" -> Expression.constant(getDirectChildObjects(context.game()));
            default -> super.getStatValue(name, context);
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

    private Set<WorldObject> getDirectChildObjects(Game game) {
        Set<WorldObject> childObjects = new HashSet<>();
        for (NetworkNode node : childNodes) {
            if (node instanceof NetworkNodeControl controlNode) {
                childObjects.add(controlNode.getObject(game));
            }
        }
        return childObjects;
    }

    private Set<WorldObject> getAllChildObjects(Game game) {
        Set<WorldObject> childObjects = new HashSet<>();
        for (NetworkNode node : childNodes) {
            if (node instanceof NetworkNodeControl controlNode) {
                childObjects.add(controlNode.getObject(game));
            } else if (node instanceof NetworkNodeGroup groupNode) {
                childObjects.addAll(groupNode.getAllChildObjects(game));
            }
        }
        return childObjects;
    }

}
