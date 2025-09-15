package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
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

    public StatHolder getHolder(Game game, Context context) {
        if (holderExpression != null) {
            Expression expressionResult = computeHolderExpression(game, context);
            if (expressionResult.getDataType() != Expression.DataType.STAT_HOLDER) throw new IllegalArgumentException("StatHolderReference expression is not a stat holder");
            return expressionResult.getValueStatHolder();
        } else if (parentReference != null) {
            Expression holderID = computeHolderID(game, context);
            if (holderID != null && holderID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("StatHolderReference holderID is not a string");
            String holderIDValue = holderID != null ? holderID.getValueString() : null;
            return parentReference.getHolder(game, context).getSubHolder(holderType, holderIDValue);
        } else {
            return getTopLevelHolder(game, context);
        }
    }

    private StatHolder getTopLevelHolder(Game game, Context context) {
        Expression holderID = computeHolderID(game, context);
        String holderIDValue = holderID != null ? holderID.getValueString() : null;
        return switch (holderType) {
            case "object" -> game.data().getObject(holderIDValue);
            case "parentObject" -> context.getParentObject();
            case "item" -> game.data().getItemInstance(holderIDValue);
            case "itemTemplate" -> game.data().getItemTemplate(holderIDValue);
            case "parentItem" -> context.getParentItem();
            case "area" -> game.data().getArea(holderIDValue);
            case "parentArea" -> context.getParentArea();
            case "room" -> game.data().getRoom(holderIDValue);
            case "scene" -> game.data().getScene(holderIDValue);
            case "actor" -> game.data().getActor(holderIDValue);
            case "player" -> game.data().getPlayer();
            case "target" -> context.getTarget();
            default -> context.getSubject(); // "subject"
        };
    }

    private Expression computeHolderID(Game game, Context context) {
        Expression holderID = null;
        if (holderIDScript != null) {
            Script.ScriptReturnData holderIDResult = holderIDScript.execute(game, context);
            // TODO - Possibly replace exceptions with error log and default to null
            if (holderIDResult.error() != null) {
                throw new IllegalArgumentException("StatHolderReference holderID expression threw an error: " + holderIDResult.stackTrace());
            } else if (holderIDResult.flowStatement() != null) {
                throw new IllegalArgumentException("StatHolderReference holderID contains an unexpected flow statement");
            }
            holderID = holderIDResult.value();
        }
        return holderID;
    }

    private Expression computeHolderExpression(Game game, Context context) {
        Expression expression = null;
        if (holderExpression != null) {
            Script.ScriptReturnData expressionResult = holderExpression.execute(game, context);
            // TODO - Possibly replace exceptions with error log and default to null
            if (expressionResult.error() != null) {
                throw new IllegalArgumentException("StatHolderReference expression threw an error: " + expressionResult.stackTrace());
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
