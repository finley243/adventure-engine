package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.action.Action;
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
    private final Action parentAction;
    private final Map<String, Variable> localVariables;

    public Context(Game game, Actor subject, Actor target) {
        this(game, subject, target, null, null, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, Map<String, Expression> localVariables) {
        this(game, subject, target, null, null, null, null, localVariables);
    }

    public Context(Game game, Actor subject, Actor target, WorldObject parentObject) {
        this(game, subject, target, parentObject, null, null, null, new HashMap<>());
    }

    public Context(Game game, WorldObject parentObject) {
        this(game, null, null, parentObject, null, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, Item parentItem) {
        this(game, subject, target, null, parentItem, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, Item parentItem, Map<String, Expression> localVariables) {
        this(game, subject, target, null, parentItem, null, null, localVariables);
    }

    public Context(Game game, Item parentItem) {
        this(game, null, null, null, parentItem, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, AttackTarget attackTarget) {
        this(game, subject, (attackTarget instanceof Actor) ? (Actor) attackTarget : null, (attackTarget instanceof WorldObject) ? (WorldObject) attackTarget : null);
    }

    public Context(Game game, Actor subject, AttackTarget attackTarget, Item parentItem) {
        this(game, subject, (attackTarget instanceof Actor) ? (Actor) attackTarget : null, (attackTarget instanceof WorldObject) ? (WorldObject) attackTarget : null, parentItem, null, null, new HashMap<>());
    }

    public Context(Game game, Actor subject, Actor target, WorldObject parentObject, Item parentItem, Area parentArea, Action parentAction, Map<String, Expression> localVariables) {
        this.game = game;
        this.subject = subject;
        this.target = target;
        this.parentObject = parentObject;
        this.parentItem = parentItem;
        this.parentArea = parentArea;
        this.parentAction = parentAction;
        this.localVariables = new HashMap<>();
        for (Map.Entry<String, Expression> parameter : localVariables.entrySet()) {
            this.localVariables.put(parameter.getKey(), new Variable(parameter.getValue()));
        }
    }

    public Context(Context context, Actor subject, Actor target) {
        this.game = context.game;
        this.subject = subject;
        this.target = target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parentArea = context.parentArea;
        this.parentAction = context.parentAction;
        this.localVariables = new HashMap<>(context.localVariables);
    }

    public Context(Context context, Map<String, Expression> addedParameters) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parentArea = context.parentArea;
        this.parentAction = context.parentAction;
        this.localVariables = new HashMap<>();
        this.localVariables.putAll(context.localVariables);
        for (Map.Entry<String, Expression> parameter : addedParameters.entrySet()) {
            if (this.localVariables.containsKey(parameter.getKey())) {
                this.localVariables.get(parameter.getKey()).setExpression(parameter.getValue());
            } else {
                this.localVariables.put(parameter.getKey(), new Variable(parameter.getValue()));
            }
        }
    }

    public Context(Context context, boolean keepLocalVariables) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parentArea = context.parentArea;
        this.parentAction = context.parentAction;
        if (keepLocalVariables) {
            this.localVariables = new HashMap<>(context.localVariables);
        } else {
            this.localVariables = new HashMap<>();
        }
    }

    public Context(Context context, Map<String, Expression> addedParameters, Item parentItem) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = parentItem;
        this.parentArea = context.parentArea;
        this.parentAction = context.parentAction;
        this.localVariables = new HashMap<>();
        this.localVariables.putAll(context.localVariables);
        for (Map.Entry<String, Expression> parameter : addedParameters.entrySet()) {
            if (this.localVariables.containsKey(parameter.getKey())) {
                this.localVariables.get(parameter.getKey()).setExpression(parameter.getValue());
            } else {
                this.localVariables.put(parameter.getKey(), new Variable(parameter.getValue()));
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

    public Action getParentAction() {
        return parentAction;
    }

    public Map<String, Variable> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariable(String name, Expression value) {
        if (localVariables.containsKey(name)) {
            localVariables.get(name).setExpression(value);
        } else {
            localVariables.put(name, new Variable(value));
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
        for (Map.Entry<String, Context.Variable> entry : this.getLocalVariables().entrySet()) {
            if (entry.getValue().getExpression().getDataType() == Expression.DataType.NOUN) {
                nounMap.put(entry.getKey(), entry.getValue().getExpression().getValueNoun());
            }
        }
        return nounMap;
    }

    public Map<String, String> getTextVarMap() {
        Map<String, String> textVarValues = new HashMap<>();
        for (Map.Entry<String, Context.Variable> entry : this.getLocalVariables().entrySet()) {
            if (entry.getValue().getExpression().getDataType() == Expression.DataType.STRING) {
                textVarValues.put(entry.getKey(), entry.getValue().getExpression().getValueString());
            }
        }
        return textVarValues;
    }

    /**
     * The Variable class is a container that stores a single Expression. The purpose is to allow the value of a local
     * variable that was first declared in an outer scope to be modified within an inner scope. Thus, a Variable can
     * be set to reference a different Expression object, and any contexts that have access to the Variable automatically
     * have access to the new Expression.
     */
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
