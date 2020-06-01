package Broker;

import Logger.Logger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import static Logger.LogType.ERROR;
import static Logger.LogType.INFO;

public class TopicWriter {
    private RandomAccessFile buffer;
    private Topic topic;
    private HashMap<String, Transaction> transactions;
    private final Monitor transactionsMonitor = new Monitor(), writeMonitor = new Monitor();
    private Logger logger;

    TopicWriter(Topic topic) {
        this.topic = topic;
        transactions = new HashMap<>();
        try {
            buffer = new RandomAccessFile(topic.getTopicFile(), "rws");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logger = Logger.getInstance(topic.getName());
    }

    void put(String producerName, int value) {
        if(value <= 0) {
            handleTransactionOperation(producerName, value);
        }
        else {
            handleInsertOperation(producerName, value);
        }
    }

    private void handleTransactionOperation(String producerName, int value) {
        switch (value) {
            case 0:
                startTransaction(producerName);
                break;
            case -1:
                commitTransaction(producerName);
                break;
            case -2:
                cancelTransaction(producerName);
        }
    }

    private void handleInsertOperation(String producerName, int value) {
        synchronized (transactionsMonitor) {
            if (transactions.containsKey(producerName)) {
                transactions.get(producerName).put(value);
                return;
            }
        }
        synchronized (writeMonitor) {
            writeValue(value);
        }
    }

    private void addTransaction(String producerName) {
        synchronized (transactionsMonitor) {
            transactions.put(producerName, new Transaction(this));
        }
    }

    private void startTransaction(String producerName) {
        synchronized (transactionsMonitor) {
            if (transactions.containsKey(producerName)) {
                logger.requestLog(ERROR, producerName + ": didn't finalize last transaction");
                commitTransaction(producerName);
                transactions.remove(producerName);
            }
        }
        addTransaction(producerName);
        logger.requestLog(INFO, producerName + ": started transaction");
    }

    private void commitTransaction(String producerName) {
        if (!transactions.containsKey(producerName)) {
            logger.requestLog(ERROR, producerName + ": no transaction to commit.");
            return;
        }

        Monitor commitMonitor = new Monitor();
        synchronized (transactionsMonitor) {
            transactions.get(producerName).commit(commitMonitor);
            transactions.remove(producerName);
        }
        synchronized (writeMonitor) {
            commitMonitor.doWait();
        }
        logger.requestLog(INFO, producerName + ": commited transaction.");
    }

    private void cancelTransaction(String producerName) {
        if(transactions.containsKey(producerName)) {
            transactions.remove(producerName);
            logger.requestLog(INFO, producerName + ": canceled transaction.");
        }
        else {
            logger.requestLog(ERROR, producerName + ": no transaction to cancel.");
        }
    }

    void writeValue(int value) {
        try {
            buffer.writeInt(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Monitor getWriteMonitor() {
        return writeMonitor;
    }
}
