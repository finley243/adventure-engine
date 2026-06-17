package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Actor subject;
    private final Actor target;
    private final WorldObject parentObject;
    private final Item parentItem;
    private final Area parentArea;
    private final Action parentAction;
    private final Map<String, Variable> localVariables;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder from(Context context){
        return new Builder(context);
    }

    private Context(Builder builder) {
        this.subject = builder.subject;
        this.target = builder.target;
        this.parentObject = builder.parentObject;
        this.parentItem = builder.parentItem;
        this.parentArea = builder.parentArea;
        this.parentAction = builder.parentAction;
        this.localVariables = new HashMap<>(builder.localVariables);
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

    public TextContext generateTextContext() {
        return new TextContext(this.getTextVarMap(), this.getContextNounMap());
    }

    private Map<String, Noun> getContextNounMap() {
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
        if (this.getParentArea() != null) {
            nounMap.put("area", this.getParentArea());
        }
        for (Map.Entry<String, Context.Variable> entry : this.getLocalVariables().entrySet()) {
            if (entry.getValue().getExpression() != null && entry.getValue().getExpression().getDataType() == Expression.DataType.NOUN) {
                nounMap.put(entry.getKey(), entry.getValue().getExpression().getValueNoun());
            }
        }
        return nounMap;
    }

    private Map<String, String> getTextVarMap() {
        Map<String, String> textVarValues = new HashMap<>();
        for (Map.Entry<String, Context.Variable> entry : this.getLocalVariables().entrySet()) {
            if (entry.getValue().getExpression() == null) continue;
            if (entry.getValue().getExpression().getDataType() == Expression.DataType.STRING) {
                textVarValues.put(entry.getKey(), entry.getValue().getExpression().getValueString());
            } else if (entry.getValue().getExpression().getDataType() == Expression.DataType.INTEGER) {
                textVarValues.put(entry.getKey(), String.valueOf(entry.getValue().getExpression().getValueInteger()));
            } else if (entry.getValue().getExpression().getDataType() == Expression.DataType.FLOAT) {
                textVarValues.put(entry.getKey(), String.valueOf(entry.getValue().getExpression().getValueFloat()));
            } else if (entry.getValue().getExpression().getDataType() == Expression.DataType.BOOLEAN) {
                textVarValues.put(entry.getKey(), String.valueOf(entry.getValue().getExpression().getValueBoolean()));
            }
        }
        return textVarValues;
    }

    public static class Builder {

        private Actor subject;
        private Actor target;
        private WorldObject parentObject;
        private Item parentItem;
        private Area parentArea;
        private Action parentAction;
        private Map<String, Variable> localVariables =  new HashMap<>();

        private Builder() {}

        private Builder(Context context) {
            this.subject = context.subject;
            this.target = context.target;
            this.parentObject = context.parentObject;
            this.parentItem = context.parentItem;
            this.parentArea = context.parentArea;
            this.parentAction = context.parentAction;
            this.localVariables.putAll(context.localVariables);
        }

        public Builder subject(Actor subject) {
            this.subject = subject;
            return this;
        }

        public Builder target(Actor target) {
            this.target = target;
            return this;
        }

        public Builder attackTarget(AttackTarget attackTarget) {
            this.target = (attackTarget instanceof Actor) ? (Actor) attackTarget : null;
            this.parentObject = (attackTarget instanceof WorldObject) ? (WorldObject) attackTarget : null;
            return this;
        }

        public Builder parentObject(WorldObject parentObject) {
            this.parentObject = parentObject;
            return this;
        }

        public Builder parentItem(Item parentItem) {
            this.parentItem = parentItem;
            return this;
        }

        public Builder parentArea(Area parentArea) {
            this.parentArea = parentArea;
            return this;
        }

        public Builder parentAction(Action parentAction) {
            this.parentAction = parentAction;
            return this;
        }

        public Builder clearVariables() {
            this.localVariables.clear();
            return this;
        }

        public Builder addVariable(String name, Expression value) {
            if (this.localVariables.containsKey(name)) {
                this.localVariables.get(name).setExpression(value);
            } else {
                this.localVariables.put(name, new Variable(value));
            }
            return this;
        }

        public Builder addVariables(Map<String, Expression> variables) {
            for (Map.Entry<String, Expression> entry : variables.entrySet()) {
                if (this.localVariables.containsKey(entry.getKey())) {
                    this.localVariables.get(entry.getKey()).setExpression(entry.getValue());
                } else {
                    this.localVariables.put(entry.getKey(), new Variable(entry.getValue()));
                }
            }
            return this;
        }

        public Context build() {
            return new Context(this);
        }

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
            this.expression = expression;
        }

        public Expression getExpression() {
            return expression;
        }

        public void setExpression(Expression expression) {
            this.expression = expression;
        }
    }

}

