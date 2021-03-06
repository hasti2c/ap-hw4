package Broker;

import Logger.Logger;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import static Logger.LogType.INFO;

public class TopicReader {
    private Topic topic;
    private RandomAccessFile topicFile;
    private final Monitor readMonitor = new Monitor(), transactionMonitor = new Monitor();

    TopicReader(Topic topic) {
        this.topic = topic;
        try {
            topicFile = new RandomAccessFile(topic.getTopicFile(), "rws");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Integer> get(String consumerName) {
        synchronized (transactionMonitor) {
            ArrayList<Integer> ret = new ArrayList<>();
            int value = readValue();
            if (value == -3)
                return ret;
            if (value != 0) {
                ret.add(value);
                return ret;
            }
            return readTransaction(consumerName);
        }
    }

    private ArrayList<Integer> readTransaction(String consumerName) {
        ArrayList<Integer> ret = new ArrayList<>();
        int value = readValue();
        while (value != -1) {
            ret.add(value);
            value = readValue();
        }
        Logger.getInstance(topic).requestLog(INFO, consumerName + ": read transaction");
        return ret;
    }

    //TODO don't move pointer by hand
    private int readValue() {
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
