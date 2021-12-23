package com.github.finley243.adventureengine.world;

import java.util.Set;

public class Lock {

    private final Set<String> keyItems;
    private final boolean isNetworked;
    private final boolean canPick;

    private boolean isLocked;

    public Lock(boolean startLocked, Set<String> keyItems, boolean isNetworked, boolean canPick) {
        this.isLocked = startLocked;
        this.keyItems = keyItems;
        this.isNetworked = isNetworked;
        this.canPick = canPick;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isNetworked() {
        return isNetworked;
    }

    public boolean canPick() {
        return canPick;
    }

    public Set<String> getKeyItems() {
        return keyItems;
    }

}
