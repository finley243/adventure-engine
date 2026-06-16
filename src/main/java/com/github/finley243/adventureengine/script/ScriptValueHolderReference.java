package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptValueHolderReference {

    private final String holderType;
    private final Script holderIDScript;
    private final ScriptValueHolderReference parentReference;
    private final Script holderExpression;

    public ScriptValueHolderReference(String holderType, Script holderIDScript, ScriptValueHolderReference parentReference, Script holderExpression) {
        this.holderType = holderType;
        this.holderIDScript = holderIDScript;
        this.parentReference = parentReference;
        this.holderExpression = holderExpression;
    }

    public ScriptValueHolder getHolder(ScriptRuntime scriptRuntime, Context context) {
        if (holderExpression != null) {
            Expression expressionResult = computeHolderExpression(scriptRuntime, context);
            if (expressionResult.getDataType() != Expression.DataType.STAT_HOLDER) throw new IllegalArgumentException("StatHolderReference expression is not a stat holder");
            return expressionResult.getValueStatHolder();
        } else if (parentReference != null) {
            Expression holderID = computeHolderID(scriptRuntime, context);
            if (holderID != null && holderID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("StatHolderReference holderID is not a string");
            String holderIDValue = holderID != null ? holderID.getValueString() : null;
            return parentReference.getHolder(scriptRuntime, context).getSubHolder(holderType, holderIDValue);
        } else {
            return getTopLevelHolder(scriptRuntime, context);
        }
    }

    private ScriptValueHolder getTopLevelHolder(ScriptRuntime scriptRuntime, Context context) {
        Expression holderID = computeHolderID(scriptRuntime, context);
        String holderIDValue = holderID != null ? holderID.getValueString() : null;
        return switch (holderType) {
            case "object" -> scriptRuntime.getObject(holderIDValue);
            case "parentObject" -> context.getParentObject();
            case "item" -> scriptRuntime.getItem(holderIDValue);
            case "itemTemplate" -> scriptRuntime.getItemTemplate(holderIDValue);
            case "parentItem" -> context.getParentItem();
            case "area" -> scriptRuntime.getArea(holderIDValue);
            case "parentArea" -> context.getParentArea();
            case "room" -> scriptRuntime.getRoom(holderIDValue);
            case "scene" -> scriptRuntime.getScene(holderIDValue);
            case "actor" -> scriptRuntime.getActor(holderIDValue);
            case "player" -> scriptRuntime.getPlayer();
            case "target" -> context.getTarget();
            default -> context.getSubject(); // "subject"
        };
    }

    private Expression computeHolderID(ScriptRuntime scriptRuntime, Context context) {
        Expression holderID = null;
        if (holderIDScript != null) {
            Script.ScriptReturnData holderIDResult = holderIDScript.execute(scriptRuntime, context);
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

    private Expression computeHolderExpression(ScriptRuntime scriptRuntime, Context context) {
        Expression expression = null;
        if (holderExpression != null) {
            Script.ScriptReturnData expressionResult = holderExpression.execute(scriptRuntime, context);
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
