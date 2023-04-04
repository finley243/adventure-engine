package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.variable.Variable;

public class StatHolderReference {

    private final String holderType;
    private final Variable holderID;
    private final String subType;
    private final Variable subID;

    public StatHolderReference(String holderType, Variable holderID, String subType, Variable subID) {
        if (holderID != null && holderID.getDataType() != Variable.DataType.STRING) throw new IllegalArgumentException("StatHolderReference holderID must be a string");
        if (subID != null && subID.getDataType() != Variable.DataType.STRING) throw new IllegalArgumentException("StatHolderReference subID must be a string");
        this.holderType = holderType;
        this.holderID = holderID;
        this.subType = subType;
        this.subID = subID;
    }

    public StatHolder getHolder(ContextScript context) {
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

    private StatHolder getParentHolder(ContextScript context) {
        return switch (holderType) {
            case "object" -> context.game().data().getObject(holderID.getValueString(context));
            case "parentObject" -> context.getParentObject();
            case "item" -> context.game().data().getItemState(holderID.getValueString(context));
            case "itemTemplate" -> context.game().data().getItem(holderID.getValueString(context));
            case "parentItem" -> context.getParentItem();
            case "area" -> context.game().data().getArea(holderID.getValueString(context));
            case "room" -> context.game().data().getRoom(holderID.getValueString(context));
            case "scene" -> context.game().data().getScene(holderID.getValueString(context));
            case "actor" -> context.game().data().getActor(holderID.getValueString(context));
            case "player" -> context.game().data().getPlayer();
            case "target" -> context.getTarget();
            case "subject", default -> context.getSubject();
        };
    }

}
