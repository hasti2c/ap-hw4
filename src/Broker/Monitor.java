package Broker;

import java.util.ArrayList;

public class Monitor {
    private boolean isSignalled = false;
    private int waiting = 0;

    public void doNotify() {
        synchronized (this) {
            notify();
            isSignalled = true;
            waiting--;
        }
    }


    public boolean isSignalled() {
        return isSignalled;
    }

    public boolean clear() {
        synchronized (this) {
            if (waiting == 0) {
                isSignalled = false;
                return true;
            }
            return false;
        }
    }

    public void doWait() {
        synchronized (this) {
            waiting++;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
