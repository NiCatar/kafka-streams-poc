package com.github.nicatar.customer.balance.streams.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class KafkaConfigs {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static final Properties getKafkaStreamsConfig(String applicationId) {
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

//        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0); // No para produccion

//        config.put("apicurio.registry.url", "http://localhost:8081/api");
//        config.put("apicurio.registry.avro-datum-provider", ReflectAvroDatumProvider.class);
//        config.put("apicurio.registry.global-id", FindBySchemaIdStrategy.class);
//        config.put("apicurio.registry.artifact-id", RecordIdStrategy.class);

        return config;
    }

    public static final Properties getKafkaStreamsConfigCustomerBalance() {
        return getKafkaStreamsConfig("customer-balance");
    }

    public static final Properties getKafkaStreamsConfigCustomerEnrichment() {
        return getKafkaStreamsConfig("customer-enrichment");
    }
}
