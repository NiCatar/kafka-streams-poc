package com.github.nicatar.customer.transaction.producer.client;

import com.github.nicatar.customer.transaction.producer.CustomerTransactionProducer;
import com.github.nicatar.customer.transaction.producer.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaClient {
    private static final Logger logger = LoggerFactory.getLogger(KafkaClient.class.getName());

    public static KafkaProducer<String, String> getKafkaProducer() {
        Properties config = KafkaConfig.getProducerConfig();
        logger.info("Generating KafkaProducer");
        return new KafkaProducer<>(config);
    }


}
