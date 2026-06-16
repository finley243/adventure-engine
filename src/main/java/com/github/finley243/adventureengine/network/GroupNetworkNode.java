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

public class GroupNetworkNode extends NetworkNode {

    private final Set<NetworkNode> childNodes;

    public GroupNetworkNode(String ID, String name, Set<NetworkNode> childNodes) {
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
    public Expression getScriptValue(String name, Context context) {
        return switch (name) {
            case "childNodes" -> Expression.set(getAllChildNodes(), Expression::valueHolder);
            case "directChildNodes" -> Expression.set(childNodes, Expression::valueHolder);
            case "childObjects" -> Expression.set(getAllChildObjects(), Expression::valueHolder);
            case "directChildObjects" -> Expression.set(getDirectChildObjects(), Expression::valueHolder);
            default -> super.getScriptValue(name, context);
        };
    }

    private Set<NetworkNode> getAllChildNodes() {
        Set<NetworkNode> children = new HashSet<>(childNodes);
        for (NetworkNode node : childNodes) {
            if (node instanceof GroupNetworkNode groupNode) {
                children.addAll(groupNode.getAllChildNodes());
            }
        }
        return children;
    }

    private Set<WorldObject> getDirectChildObjects() {
        Set<WorldObject> childObjects = new HashSet<>();
        for (NetworkNode node : childNodes) {
            if (node instanceof ControlNetworkNode controlNode) {
                childObjects.add(controlNode.getObject());
            }
        }
        return childObjects;
    }

    private Set<WorldObject> getAllChildObjects() {
        Set<WorldObject> childObjects = new HashSet<>();
        for (NetworkNode node : childNodes) {
            if (node instanceof ControlNetworkNode controlNode) {
                childObjects.add(controlNode.getObject());
            } else if (node instanceof GroupNetworkNode groupNode) {
                childObjects.addAll(groupNode.getAllChildObjects());
            }
        }
        return childObjects;
    }

}
