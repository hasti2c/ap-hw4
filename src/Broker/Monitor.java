package Broker;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;

public class Monitor {
    private boolean isSignalled = false;
    private ArrayList<Thread> waiting = new ArrayList<>();

    public void doNotify() {
        synchronized (this) {
            isSignalled = true;
            notify();
        }
    }

    private void clear() {
        synchronized (this) {
            waiting.remove(Thread.currentThread());
            if (waiting.size() == 0) {
                isSignalled = false;
            }
        }
    }

    public void doWait() {
        synchronized (this) {
            System.out.println("hi");
            System.out.println(waiting.size());
            while (!isSignalled) {
                System.out.println("hi2");
                try {
                    Thread thread = Thread.currentThread();
                    if (!waiting.contains(thread))
                        waiting.add(thread);
                    System.out.println("$" + waiting.size());
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("$$" + waiting.size());
            clear();
            System.out.println("$$$" + waiting.size());
        }
    }
}
