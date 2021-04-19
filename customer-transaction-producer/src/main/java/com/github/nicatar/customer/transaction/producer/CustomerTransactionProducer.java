package com.github.nicatar.customer.transaction.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nicatar.customer.transaction.producer.client.KafkaClient;
import com.github.nicatar.customer.transaction.producer.model.Customer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class CustomerTransactionProducer {
    Logger logger = LoggerFactory.getLogger(CustomerTransactionProducer.class.getName());

    Queue<Customer> ch = new LinkedBlockingQueue<Customer>();

    public static void main(String[] args) {
        new CustomerTransactionProducer().run();
    }

    public void run() {
        logger.info("Running application");
        KafkaProducer<String, String> producer = KafkaClient.getKafkaProducer();
        addShutdownHook(producer, "closing producer...");

        Thread dataFactory = new Thread(() -> dataFactory(ch));
        dataFactory.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> dataFactory.interrupt()));

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> executorService.shutdown()));

        Runnable runner = () -> this.sendEnqueueCustomers(producer, ch, 1200);

        ScheduledFuture<?> schedule = executorService.scheduleAtFixedRate(runner, 1, 1, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> schedule.cancel(false)));

    }

    private void sendEnqueueCustomers(KafkaProducer<String, String> producer, Queue<Customer> ch, int cant) {
        if(ch == null) return;
        logger.info("Send: {} records", Math.min(ch.size(), cant));
        ObjectMapper objectMapper = new ObjectMapper();

        for(int i = 0; i < cant; i++) {
            Customer currentCustomer = ch.poll();
            if(currentCustomer == null) break;

            try {
                String body = objectMapper.writeValueAsString(currentCustomer);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>("customers_transfer", body);
                producer.send(record, (recordMetadata, e) -> {
                    if (e != null) e.printStackTrace();
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }

    }

    private void addShutdownHook(AutoCloseable closeable, String message) {
        if (closeable != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info(message);
                    closeable.close();
                } catch (Exception var3) {
                    this.logger.error("Error processing closeable", var3);
                }

            }));
        }
    }

    private void dataFactory(Queue<Customer> ch) {

        List<String> custormersName = Arrays.asList("Nicolas", "Hector", "Noelia", "Magali", "Pepe", "Pepon");
        logger.info("Running data factory");

        while(!Thread.interrupted()) {
            if(ch.size() > 10000) {
                continue;
            }

            for (String name : custormersName) {
                int randomMoney = ((int) (Math.random() * (10000 - 1))) + 1;
                ch.offer(new Customer(name, randomMoney, new Date()));
            }

        }
        logger.info("Exiting data factory");
    }

}
