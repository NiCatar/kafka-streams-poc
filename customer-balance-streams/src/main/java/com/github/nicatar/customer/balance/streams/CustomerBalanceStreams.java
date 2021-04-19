package com.github.nicatar.customer.balance.streams;

import com.github.nicatar.customer.balance.streams.config.KafkaConfigs;
import com.github.nicatar.customer.balance.streams.topology.CustomerEnrichmentTopology;
import com.github.nicatar.customer.balance.streams.topology.TrasactionsBalanceMoneyTopology;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class CustomerBalanceStreams {
    private Logger logger = LoggerFactory.getLogger(CustomerBalanceStreams.class.getName());

    public static void main(String[] args) {
        new CustomerBalanceStreams().run();
    }

    public void run() {
        logger.info("Initializing app");
        final CountDownLatch latch = new CountDownLatch(2);

        // Customer balance
        StreamsBuilder builderCustomerBalance = new StreamsBuilder();
        TrasactionsBalanceMoneyTopology transactionBalanceTopology = new TrasactionsBalanceMoneyTopology();
        transactionBalanceTopology.addTopologyTo(builderCustomerBalance);

        Topology topologyCustomerBalance = builderCustomerBalance.build();
        Properties kafkaStreamsConfigCustomerBalance = KafkaConfigs.getKafkaStreamsConfigCustomerBalance();
        System.out.println(topologyCustomerBalance.describe());

        KafkaStreams streamsCustomerrBalance = new KafkaStreams(topologyCustomerBalance, kafkaStreamsConfigCustomerBalance);
        System.out.println(streamsCustomerrBalance);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Closing Kafka Streams Customer balance");

            streamsCustomerrBalance.close();
            latch.countDown();
        }));

        // Customer enrichment
        StreamsBuilder builderCustomerEnrichment = new StreamsBuilder();
        CustomerEnrichmentTopology customerEnrichmentTopology = new CustomerEnrichmentTopology();
        customerEnrichmentTopology.addTopologyTo(builderCustomerEnrichment);

        Topology topologyCustomerEnrichment = builderCustomerEnrichment.build();
        Properties kafkaStreamsConfigCustomerEnrichment = KafkaConfigs.getKafkaStreamsConfigCustomerEnrichment();
        System.out.println(topologyCustomerEnrichment.describe());

        KafkaStreams streamsCustomerEnrichment = new KafkaStreams(topologyCustomerEnrichment, kafkaStreamsConfigCustomerEnrichment);
        System.out.println(streamsCustomerEnrichment);



        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Closing Kafka Streams Customer Enrichment");

            streamsCustomerEnrichment.close();
            latch.countDown();
        }));


        try {
            streamsCustomerrBalance.cleanUp(); // no en produccion
            streamsCustomerrBalance.start();

            streamsCustomerEnrichment.cleanUp(); // no en prod
            streamsCustomerEnrichment.start();

            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }



}
