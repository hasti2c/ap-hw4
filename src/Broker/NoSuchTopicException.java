package Broker;

public class NoSuchTopicException extends Exception {
    NoSuchTopicException(String topicName) {
        super("There is no registered topic with name " + topicName + " in broker.");
    }
}
