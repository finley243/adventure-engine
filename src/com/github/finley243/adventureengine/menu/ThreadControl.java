package com.github.finley243.adventureengine.menu;

public class ThreadControl {

    private boolean pauseThread;

    public synchronized void pause() {
        this.pauseThread = true;
        while (pauseThread) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void unpause() {
        this.pauseThread = false;
        notifyAll();
    }

}
