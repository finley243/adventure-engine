package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Game game;
    private final Actor subject;
    private final Actor target;
    private final WorldObject parentObject;
    private final Item parentItem;
    private final Map<String, Variable> parameters;

    public Context(Game game, Actor subject, Actor target) {
        this(game, subject, target, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, WorldObject parentObject) {
        this(game, subject, target, parentObject, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, Item parentItem) {
        this(game, subject, target, null, parentItem, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, WorldObject parentObject, Item parentItem, Map<String, Expression> parameters) {
        this.game = game;
        this.subject = subject;
        this.target = target;
        this.parentObject = parentObject;
        this.parentItem = parentItem;
        this.parameters = new HashMap<>();
        for (Map.Entry<String, Expression> parameter : parameters.entrySet()) {
            this.parameters.put(parameter.getKey(), new Variable(parameter.getValue()));
        }
    }

    public Context(Context context, Actor subject, Actor target) {
        this.game = context.game;
        this.subject = subject;
        this.target = target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parameters = new HashMap<>(context.parameters);
    }

    public Context(Context context, Map<String, Expression> addedParameters) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parameters = new HashMap<>();
        this.parameters.putAll(context.parameters);
        for (Map.Entry<String, Expression> parameter : addedParameters.entrySet()) {
            if (this.parameters.containsKey(parameter.getKey())) {
                this.parameters.get(parameter.getKey()).setExpression(parameter.getValue());
            } else {
                this.parameters.put(parameter.getKey(), new Variable(parameter.getValue()));
            }
        }
    }

    public Game game() {
        return game;
    }

    public Actor getSubject() {
        return subject;
    }

    public Actor getTarget() {
        return target;
    }

    public WorldObject getParentObject() {
        return parentObject;
    }

    public Item getParentItem() {
        return parentItem;
    }

    public Map<String, Variable> getParameters() {
        return parameters;
    }

    public static class Variable {
        private Expression expression;

        public Variable(Expression expression) {
            if (expression == null) throw new IllegalArgumentException("Expression cannot be initialized to null");
            this.expression = expression;
        }

        public Expression getExpression() {
            return expression;
        }

        public void setExpression(Expression expression) {
            if (expression == null) throw new IllegalArgumentException("Expression cannot be set to null");
            this.expression = expression;
        }
    }

}
