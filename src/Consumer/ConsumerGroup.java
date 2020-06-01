package Consumer;

import Broker.MessageBroker;
import Broker.Monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ConsumerGroup extends Thread {
    private ArrayList<Consumer> consumers;
    private MessageBroker messageBroker;
    private String topicName, groupName;
    private int numberOfConsumers;
    private File consumerGroupFile;
    private PrintWriter printWriter;
    private final Monitor writeMonitor = new Monitor();

    public ConsumerGroup(MessageBroker messageBroker, String topicName, String groupName, File consumerGroupFile, int numberOfConsumers) {
        this.messageBroker = messageBroker;
        this.consumerGroupFile = consumerGroupFile;
        this.topicName = topicName;
        this.groupName = groupName;
        this.numberOfConsumers = numberOfConsumers;
        consumers = new ArrayList<>();
    }

    private void initialize() throws FileNotFoundException {
        for (int i = 0; i < numberOfConsumers; i++) {
            String consumerName = groupName + "_" + i;
            consumers.add(new Consumer(this, consumerName, messageBroker));
        }

        printWriter = new PrintWriter(consumerGroupFile);
    }

    public void run() {
        try {
            initialize();

            for (Consumer consumer : consumers) {
                consumer.start();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void performAction(Consumer consumer, int value) {
        if (value == -3)
            return;
        synchronized (writeMonitor) {
            printWriter.println("Consumer with name " + consumer.getConsumerName() + " read the value " + value);
            printWriter.flush();
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTopicName() {
        return topicName;
    }
}

