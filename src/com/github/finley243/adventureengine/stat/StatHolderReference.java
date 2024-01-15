package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

public class StatHolderReference {

    private final String holderType;
    private final Script holderIDScript;
    private final StatHolderReference parentReference;

    public StatHolderReference(String holderType, Script holderIDScript, StatHolderReference parentReference) {
        this.holderType = holderType;
        this.holderIDScript = holderIDScript;
        this.parentReference = parentReference;
    }

    public StatHolder getHolder(Context context) {
        Expression holderID = computeHolderID(context);
        if (holderID != null && holderID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("StatHolderReference holderID must be a string");
        if (parentReference != null) {
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
            } else if (holderIDResult.isReturn()) {
                throw new IllegalArgumentException("StatHolderReference holderID contains an unexpected return statement");
            }
            holderID = holderIDResult.value();
        }
        return holderID;
    }

}
