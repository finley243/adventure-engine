package com.github.finley243.adventureengine.world.object.params;

import com.github.finley243.adventureengine.world.object.params.ComponentParams;

import java.util.Set;

public class ComponentParamsItemUse implements ComponentParams {

    private final Set<String> keyItems;

    public ComponentParamsItemUse(Set<String> keyItems) {
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
