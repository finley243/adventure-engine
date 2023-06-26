package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class StatHolderReference {

    private final String holderType;
    private final Expression holderID;
    private final String subType;
    private final Expression subID;

    public StatHolderReference(String holderType, Expression holderID, String subType, Expression subID) {
        if (holderID != null && holderID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("StatHolderReference holderID must be a string");
        if (subID != null && subID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("StatHolderReference subID must be a string");
        this.holderType = holderType;
        this.holderID = holderID;
        this.subType = subType;
        this.subID = subID;
    }

    public StatHolder getHolder(Context context) {
        StatHolder parentHolder = getParentHolder(context);
        if (parentHolder == null) throw new IllegalArgumentException("StatHolder of type '" + holderType + "' with ID '" + (holderID == null ? null : holderID.getValueString(context)) + "' is null");
        if (subType == null) {
            return parentHolder;
        }
        StatHolder subHolder = parentHolder.getSubHolder(subType, subID == null ? null : subID.getValueString(context));
        if (subHolder == null) {
            throw new IllegalArgumentException("StatHolder sub-type '" + subType + "' does not exist on holder type '" + holderType + "'");
        }
        return subHolder;
    }

    private StatHolder getParentHolder(Context context) {
        return switch (holderType) {
            case "object" -> context.game().data().getObject(holderID.getValueString(context));
            case "parentObject" -> context.getParentObject();
            case "item" -> context.game().data().getItemState(holderID.getValueString(context));
            case "itemTemplate" -> context.game().data().getItemTemplate(holderID.getValueString(context));
            case "parentItem" -> context.getParentItem();
            case "area" -> context.game().data().getArea(holderID.getValueString(context));
            case "parentArea" -> context.getParentArea();
            case "room" -> context.game().data().getRoom(holderID.getValueString(context));
            case "scene" -> context.game().data().getScene(holderID.getValueString(context));
            case "actor" -> context.game().data().getActor(holderID.getValueString(context));
            case "player" -> context.game().data().getPlayer();
            case "target" -> context.getTarget();
            case "subject", default -> context.getSubject();
        };
    }

}
