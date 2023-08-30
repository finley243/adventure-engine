package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Game game;
    private final Actor subject;
    private final Actor target;
    private final WorldObject parentObject;
    private final Item parentItem;
    private final Area parentArea;
    private final Map<String, Variable> parameters;

    public Context(Game game, Actor subject, Actor target) {
        this(game, subject, target, null, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, Map<String, Expression> parameters) {
        this(game, subject, target, null, null, null, parameters);
    }

    public Context(Game game, Actor subject, Actor target, WorldObject parentObject) {
        this(game, subject, target, parentObject, null, null, new HashMap<>());
    }

    public Context(Game game, WorldObject parentObject) {
        this(game, null, null, parentObject, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, Item parentItem) {
        this(game, subject, target, null, parentItem, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, Item parentItem, Map<String, Expression> parameters) {
        this(game, subject, target, null, parentItem, null, parameters);
    }

    public Context(Game game, Item parentItem) {
        this(game, null, null, null, parentItem, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, AttackTarget attackTarget) {
        this(game, subject, (attackTarget instanceof Actor) ? (Actor) attackTarget : null, (attackTarget instanceof WorldObject) ? (WorldObject) attackTarget : null);
    }

    public Context(Game game, Actor subject, AttackTarget attackTarget, Item parentItem) {
        this(game, subject, (attackTarget instanceof Actor) ? (Actor) attackTarget : null, (attackTarget instanceof WorldObject) ? (WorldObject) attackTarget : null, parentItem, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, WorldObject parentObject, Item parentItem, Area parentArea, Map<String, Expression> parameters) {
        this.game = game;
        this.subject = subject;
        this.target = target;
        this.parentObject = parentObject;
        this.parentItem = parentItem;
        this.parentArea = parentArea;
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
        this.parentArea = context.parentArea;
        this.parameters = new HashMap<>(context.parameters);
    }

    public Context(Context context, Map<String, Expression> addedParameters) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parentArea = context.parentArea;
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

    public Context(Context context) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parentArea = context.parentArea;
        this.parameters = new HashMap<>(context.parameters);
    }

    public Context(Context context, Map<String, Expression> addedParameters, Item parentItem) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = parentItem;
        this.parentArea = context.parentArea;
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

    public Area getParentArea() {
        return parentArea;
    }

    public Map<String, Variable> getParameters() {
        return parameters;
    }

    public void setParameter(String name, Expression value) {
        if (parameters.containsKey(name)) {
            parameters.get(name).setExpression(value);
        } else {
            parameters.put(name, new Variable(value));
        }
    }

    public Map<String, Noun> getContextNounMap() {
        Map<String, Noun> nounMap = new HashMap<>();
        if (this.getSubject() != null) {
            nounMap.put("actor", this.getSubject());
        }
        if (this.getTarget() != null) {
            nounMap.put("target", this.getTarget());
        }
        if (this.getParentObject() != null) {
            nounMap.put("object", this.getParentObject());
        }
        if (this.getParentItem() != null) {
            nounMap.put("item", this.getParentItem());
        }
        for (Map.Entry<String, Context.Variable> entry : this.getParameters().entrySet()) {
            if (entry.getValue().getExpression().getDataType() == Expression.DataType.NOUN) {
                nounMap.put(entry.getKey(), entry.getValue().getExpression().getValueNoun(this));
            }
        }
        return nounMap;
    }

    public Map<String, String> getTextVarMap() {
        Map<String, String> textVarValues = new HashMap<>();
        for (Map.Entry<String, Context.Variable> entry : this.getParameters().entrySet()) {
            if (entry.getValue().getExpression().getDataType() == Expression.DataType.STRING) {
                textVarValues.put(entry.getKey(), entry.getValue().getExpression().getValueString(this));
            }
        }
        return textVarValues;
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
