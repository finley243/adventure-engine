package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;

public class ThreadControl {

    private final Game game;
    private boolean pauseThread;

    public ThreadControl(Game game) {
        this.game = game;
    }

    public synchronized void pause() {
        this.pauseThread = true;
        while (pauseThread) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void unpause() {
        this.pauseThread = false;
        this.notify();
    }

}