package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class StatHolderReference {

    private final String holderType;
    private final Expression holderID;
    private final StatHolderReference parentReference;

    public StatHolderReference(String holderType, Expression holderID, StatHolderReference parentReference) {
        if (holderID != null && holderID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("StatHolderReference holderID must be a string");
        this.holderType = holderType;
        this.holderID = holderID;
        this.parentReference = parentReference;
    }

    public StatHolder getHolder(Context context) {
        if (parentReference != null) {
            String holderIDValue = holderID != null ? holderID.getValueString(context) : null;
            return parentReference.getHolder(context).getSubHolder(holderType, holderIDValue);
        } else {
            return getTopLevelHolder(context);
        }
    }

    private StatHolder getTopLevelHolder(Context context) {
        String holderIDValue = holderID != null ? holderID.getValueString(context) : null;
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

}
