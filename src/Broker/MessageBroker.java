package Broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageBroker {
    private Map<String, Topic> topics = new HashMap<>();
    private final Monitor topicsMonitor = new Monitor();

    private void addTopic(String name) {
        topics.put(name, new Topic(name));
    }

    public void put(String topic, String producerName, int value) {
        synchronized (topicsMonitor) {
            if (!topics.containsKey(topic))
                addTopic(topic);
        }
        topics.get(topic).put(producerName, value);
    }

    public ArrayList<Integer> get(String topic, String groupName) throws NoSuchTopicException {
        synchronized (topicsMonitor) {
            if (!topics.containsKey(topic))
                throw new NoSuchTopicException(topic);
        }
        return topics.get(topic).get(groupName);
    }
}
