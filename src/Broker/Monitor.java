package Broker;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;

public class Monitor {
    private boolean isSignalled = false;
    private volatile ArrayList<Thread> waiting = new ArrayList<>();
    private final Object o = new Object();

    public void doNotify() {
        synchronized (o) {
            isSignalled = true;
            o.notifyAll();
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
        synchronized (o) {
            System.out.println("hi");
            System.out.println(waiting.size());
            while (!isSignalled) {
                System.out.println("hi2");
                try {
                    Thread thread = Thread.currentThread();
                    if (!waiting.contains(thread))
                        waiting.add(thread);
                    System.out.println("$" + waiting.size());
                    o.wait();
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
