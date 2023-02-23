package com.github.finley243.adventureengine.world.object.params;

import java.util.Set;

public class ComponentParamsKey implements ComponentParams {

    private final Set<String> keyItems;

    public ComponentParamsKey(Set<String> keyItems) {
        this.keyItems = keyItems;
    }

    @Override
    public Object getParameter(String key) {
        if ("keyItems".equals(key)) {
            return keyItems;
        }
        return null;
    }

}
