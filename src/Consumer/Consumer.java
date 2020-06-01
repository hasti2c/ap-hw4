package Consumer;

import Broker.MessageBroker;
import Broker.NoSuchTopicException;

public class Consumer extends Thread {
    private ConsumerGroup consumerGroup;
    private String consumerName;
    private MessageBroker messageBroker;

    Consumer(ConsumerGroup consumerGroup, String consumerName, MessageBroker messageBroker) {
        this.consumerGroup = consumerGroup;
        this.consumerName = consumerName;
        this.messageBroker = messageBroker;
    }

    public int get() throws NoSuchTopicException {
        return messageBroker.get(getTopicName(), consumerGroup.getGroupName(), consumerName);
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

    public String getConsumerName() {
        return consumerName;
    }

    public String getTopicName() {
        return consumerGroup.getTopicName();
    }
}
