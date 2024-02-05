package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

public class StatHolderReference {

    private final String holderType;
    private final Script holderIDScript;
    private final StatHolderReference parentReference;
    private final Script holderExpression;

    public StatHolderReference(String holderType, Script holderIDScript, StatHolderReference parentReference, Script holderExpression) {
        this.holderType = holderType;
        this.holderIDScript = holderIDScript;
        this.parentReference = parentReference;
        this.holderExpression = holderExpression;
    }

    public StatHolder getHolder(Context context) {
        if (holderExpression != null) {
            Expression expressionResult = computeHolderExpression(context);
            if (expressionResult.getDataType() != Expression.DataType.STAT_HOLDER) throw new IllegalArgumentException("StatHolderReference expression is not a stat holder");
            return expressionResult.getValueStatHolder();
        } else if (parentReference != null) {
            Expression holderID = computeHolderID(context);
            if (holderID != null && holderID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("StatHolderReference holderID is not a string");
            String holderIDValue = holderID != null ? holderID.getValueString() : null;
            return parentReference.getHolder(context).getSubHolder(holderType, holderIDValue);
        } else {
            return getTopLevelHolder(context);
        }
    }

    private StatHolder getTopLevelHolder(Context context) {
        Expression holderID = computeHolderID(context);
        String holderIDValue = holderID != null ? holderID.getValueString() : null;
        return switch (holderType) {
            case "object" -> context.game().data().getObject(holderIDValue);
            case "parentObject" -> context.getParentObject();
            case "item" -> context.game().data().getItemInstance(holderIDValue);
            case "itemTemplate" -> context.game().data().getItemTemplate(holderIDValue);
            case "parentItem" -> context.getParentItem();
            case "area" -> context.game().data().getArea(holderIDValue);
            case "parentArea" -> context.getParentArea();
            case "room" -> context.game().data().getRoom(holderIDValue);
            case "scene" -> context.game().data().getScene(holderIDValue);
            case "actor" -> context.game().data().getActor(holderIDValue);
            case "player" -> context.game().data().getPlayer();
            case "target" -> context.getTarget();
            default -> context.getSubject(); // "subject"
        };
    }

    private Expression computeHolderID(Context context) {
        Expression holderID = null;
        if (holderIDScript != null) {
            Script.ScriptReturnData holderIDResult = holderIDScript.execute(context);
            if (holderIDResult.error() != null) {
                throw new IllegalArgumentException("StatHolderReference holderID expression threw an error");
            } else if (holderIDResult.flowStatement() != null) {
                throw new IllegalArgumentException("StatHolderReference holderID contains an unexpected flow statement");
            }
            holderID = holderIDResult.value();
        }
        return holderID;
    }

    private Expression computeHolderExpression(Context context) {
        Expression expression = null;
        if (holderExpression != null) {
            Script.ScriptReturnData expressionResult = holderExpression.execute(context);
            if (expressionResult.error() != null) {
                throw new IllegalArgumentException("StatHolderReference expression threw an error");
            } else if (expressionResult.flowStatement() != null) {
                throw new IllegalArgumentException("StatHolderReference expression contains an unexpected flow statement");
            }
            expression = expressionResult.value();
        }
        return expression;
    }

    @Override
    public String toString() {
        String localString = holderType + (holderIDScript == null ? "" : "(with ID)");
        if (parentReference == null) {
            return localString;
        } else {
            return parentReference.toString() + "." + localString;
        }
    }

}
