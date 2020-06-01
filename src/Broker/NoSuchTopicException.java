package Broker;

import Broker.Topic;

public class NoSuchTopicException extends Exception {
    NoSuchTopicException(String topicName) {
        super("There is no registered topic with name " + topicName + " in broker.");
    }
}
