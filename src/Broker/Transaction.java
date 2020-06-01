package Broker;

import java.util.LinkedList;
import java.util.Queue;

class Transaction {

    private TopicWriter topicWriter;
    private Queue<Integer> values;

    Transaction(TopicWriter topicWriter) {
        this.topicWriter = topicWriter;
        values = new LinkedList<>();
    }

    void put(int value) {
        values.add(value);
    }

    synchronized void commit(Monitor commitMonitor) {
        synchronized (topicWriter.getWriteMonitor()) {
            topicWriter.writeValue(0);
            while (!values.isEmpty()) {
                topicWriter.writeValue(values.remove());
            }
            topicWriter.writeValue(-1);
            commitMonitor.doNotify();
        }
    }
}
