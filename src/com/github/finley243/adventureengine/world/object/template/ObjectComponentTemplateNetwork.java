package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ObjectComponentTemplateNetwork extends ObjectComponentTemplate {

    private final Expression networkID;

    public ObjectComponentTemplateNetwork(boolean startEnabled, boolean actionsRestricted, Expression networkID) {
        super(startEnabled, actionsRestricted);
        if (networkID == null) throw new IllegalArgumentException("NetworkID expression is null");
        this.networkID = networkID;
    }

    public String getNetworkID(Context context) {
        if (networkID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("NetworkID expression is not a string");
        return networkID.getValueString(context);
    }

}
