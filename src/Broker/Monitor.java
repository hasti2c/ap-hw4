package Broker;

import java.util.ArrayList;

public class Monitor {
    private boolean isSignalled = false;
    private int waiting = 0;

    public synchronized void doNotify() {
        notify();
        isSignalled = true;
        waiting--;
    }


    public boolean isSignalled() {
        return isSignalled;
    }

    public static void signalAll(ArrayList<Monitor> commitMonitors) {
        for (Monitor m : commitMonitors)
            m.doNotify();
    }

    public boolean clear() {
        if (waiting == 0) {
            isSignalled = false;
            return true;
        }
        return false;
    }

    public void doWait() {
        waiting++;
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
