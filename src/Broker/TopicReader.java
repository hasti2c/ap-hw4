package Broker;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TopicReader {
    private Topic topic;
    private String groupName;
    private RandomAccessFile topicFile;
    private final Monitor readMonitor = new Monitor(), transactionMonitor = new Monitor(), commitMonitor = new Monitor();
    //private ArrayList<Monitor> commitMonitors = new ArrayList<>();
    private String transactionConsumer = null;

    TopicReader(Topic topic, String groupName) {
        this.topic = topic;
        this.groupName = groupName;
        try {
            topicFile = new RandomAccessFile(topic.getTopicFile(), "rws");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int get(String consumerName) {
        synchronized (transactionMonitor) {
            if (transactionConsumer != null)
                return getTransactionValue(consumerName);
            else
                return getInsertValue(consumerName);
        }
    }

    private int getInsertValue(String consumerName) {
        int value = readValue();
        if (value <= 0)
            handleTransactionOperation(consumerName, value);
        return value;
    }

    private int getTransactionValue(String consumerName) {
        synchronized (transactionMonitor) {
            if (transactionConsumer != null && !consumerName.equals(transactionConsumer)) {
                synchronized (commitMonitor) {
                    while (!commitMonitor.isSignalled())
                        commitMonitor.doWait();
                    commitMonitor.clear();
                }
            }
        }

        return getInsertValue(consumerName);
    }

    private void handleTransactionOperation(String consumerName, int value) {
        synchronized (transactionMonitor) {
            if (value == 0) {
                if (transactionConsumer != null)
                    commitTransaction();
                transactionConsumer = consumerName;
            } else if (value == -1)
                commitTransaction();
        }
    }

    private void commitTransaction() {
        synchronized (commitMonitor) {
            transactionConsumer = null;
            //Monitor.signalAll(commitMonitors);
            //commitMonitors.clear();
            commitMonitor.doNotify();
        }
    }

    public int readValue() {
        synchronized (readMonitor) {
            int value = 0;
            try {
                long p = topicFile.getFilePointer();
                try {
                    value = topicFile.readInt();
                    p += 4;
                } catch (EOFException e) {
                    value = -3;
                }
                topicFile.seek(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return value;
        }
    }
}
