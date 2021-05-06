package com.github.nicatar.customer.balance.streams.topology;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

public class CustomerEnrichmentTopology {

    public void addTopologyTo(StreamsBuilder builder) {
        if(builder == null) throw new IllegalArgumentException("builder cannot be null");

        final Serializer<JsonNode> jsonNodeSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonNodeDeserializer = new JsonDeserializer();

        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonNodeSerializer, jsonNodeDeserializer);

//        Consumed<String, JsonNode> consumed = Consumed.with(stringSerde, jsonSerde);
//        Produced<String, JsonNode> produced = Produced.with(stringSerde, jsonSerde);

        KStream<String, JsonNode> customerStream = builder.stream("customer_transaction_keyed", Consumed.with(Serdes.String(), jsonSerde));

        GlobalKTable<String, JsonNode> customerBalanceGlobalTable = builder.globalTable("customer_balance", Consumed.with(Serdes.String(), jsonSerde));

        KStream<String, JsonNode> customerEnrichment =
                customerStream.join(customerBalanceGlobalTable,
                        (key, value) -> key,
                        (customerTransaction, customerBalance) -> {
                            ObjectNode customerEnrrichment = customerTransaction.deepCopy();
                            customerEnrrichment.putPOJO("lastBalance", customerBalance);
                            return customerEnrrichment;
                        }

                );

        customerEnrichment.to("customer_enrichment", Produced.with(Serdes.String(), jsonSerde));

    }

}
