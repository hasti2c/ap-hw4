package Broker;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;

public class Monitor {
    private boolean isSignalled = false;

    void doNotify() {
        synchronized (this) {
            isSignalled = true;
            notifyAll();
        }
    }

    void doWait() {
        synchronized (this) {
            while (!isSignalled) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
