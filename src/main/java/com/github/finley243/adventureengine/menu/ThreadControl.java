package com.github.finley243.adventureengine.menu;

public class ThreadControl {

    private boolean pauseThread;

    public ThreadControl() {}

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