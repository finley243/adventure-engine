package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.expression.Expression;

public class ObjectComponentTemplateNetwork extends ObjectComponentTemplate {

    private final Expression networkID;

    public ObjectComponentTemplateNetwork(Game game, boolean startEnabled, boolean actionsRestricted, String name, Expression networkID) {
        super(game, startEnabled, actionsRestricted, name);
        if (networkID == null) throw new IllegalArgumentException("NetworkID expression is null");
        if (networkID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("NetworkID expression is not a string");
        this.networkID = networkID;
    }

    public Expression getNetworkID() {
        return networkID;
    }

}
