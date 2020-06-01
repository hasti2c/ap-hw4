package Consumer;

import Broker.MessageBroker;
import Broker.NoSuchTopicException;

import java.util.ArrayList;

public class Consumer extends Thread {
    private ConsumerGroup consumerGroup;
    private String consumerName;
    private MessageBroker messageBroker;

    Consumer(ConsumerGroup consumerGroup, String consumerName, MessageBroker messageBroker) {
        this.consumerGroup = consumerGroup;
        this.consumerName = consumerName;
        this.messageBroker = messageBroker;
    }

    private ArrayList<Integer> get() throws NoSuchTopicException {
        return messageBroker.get(getTopicName(), consumerGroup.getGroupName());
    }

    public void run() {
        while(true) {
            try {
                consumerGroup.performAction(this, get());
            } catch (NoSuchTopicException e) {
                e.printStackTrace();
            }
        }
    }

    String getConsumerName() {
        return consumerName;
    }

    private String getTopicName() {
        return consumerGroup.getTopicName();
    }
}
