package Broker;

import java.io.File;
import java.util.HashMap;

public class Topic {
    private String name;

    private File topicFile;
    private TopicWriter topicWriter;
    private HashMap<String, TopicReader> topicReaders;
    private final Monitor topicReadersMonitor = new Monitor();

    Topic(String name) {
        this.name = name;

        topicFile = new File(name + ".dat");
        topicWriter = new TopicWriter(this);
        topicReaders = new HashMap<>();
    }

    public File getTopicFile() {
        return topicFile;
    }

    private void addGroup(String groupName) {
        topicReaders.put(groupName, new TopicReader(this, groupName));
    }

    public int get(String groupName, String consumerName) {
        synchronized (topicReadersMonitor) {
            if(!topicReaders.containsKey(groupName)) {
                addGroup(groupName);
            }
        }
        return topicReaders.get(groupName).get(consumerName);
    }

    public void put(String producerName, int value) {
        topicWriter.put(producerName, value);
    }
}
