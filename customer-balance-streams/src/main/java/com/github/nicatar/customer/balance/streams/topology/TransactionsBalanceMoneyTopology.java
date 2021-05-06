package com.github.nicatar.customer.balance.streams.topology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.nicatar.customer.balance.streams.model.CustomerDto;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.time.Instant;
import java.util.Objects;

public class TransactionsBalanceMoneyTopology {

//    private ObjectMapper om = new ObjectMapper();

    /**
     * Consume from customer_transfer (customer json format)
     * to customer_balance (key name, value customer json format)
     * @param builder
     */
    public void addTopologyTo(StreamsBuilder builder) {
        if(builder == null) throw new IllegalArgumentException("builder cannot be null");

        final Serializer<JsonNode> jsonNodeSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonNodeDeserializer = new JsonDeserializer();

        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonNodeSerializer, jsonNodeDeserializer);

        //(null, valor)
        KStream<String, JsonNode> customersJsonValues = builder.stream("customers_transfer", Consumed.with(Serdes.String(), jsonSerde));

        //(nombre, valor)
        KStream<String, JsonNode> customerKeyed = customersJsonValues.selectKey(this::getCustomerName, Named.as("select-name-as-key"));

        customerKeyed.to("customer_transaction_keyed");

        customerKeyed = builder.stream("customer_transaction_keyed", Consumed.with(Serdes.String(), jsonSerde));

        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0);
        initialBalance.put("time", Instant.ofEpochMilli(0L).toEpochMilli());

        KTable<String, JsonNode> values = customerKeyed
                //(pepe, 1)(pepe, 1)( pedro, 1)(raul,1) -> ((pepe,1)(pepe,1)) ((pedro,1)) ((raul,1))
                .groupByKey()
                .aggregate(
                        () -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction, balance),
                        Named.as("customer-balance-aggregate"),
                        Materialized.with(Serdes.String(), jsonSerde)
                );

        values.toStream(Named.as("table_to_streamable"))
                .to("customer_balance", Produced.with(Serdes.String(), jsonSerde));

    }

    private String getCustomerName(String key, JsonNode customer) {
//        System.out.println("name as key -- " + customer);
        return customer.get("Name").asText();
    }

    private JsonNode newBalance(JsonNode transaction, JsonNode balance) {

        ObjectNode newBalance = JsonNodeFactory.instance.objectNode();
        newBalance.put("count", balance.get("count").asInt() + 1);
        newBalance.put("balance", Math.abs(balance.get("balance").asInt()) + Math.abs(transaction.get("amount").asInt()));

        long balanceEpoch = balance.get("time").asLong();
        long transactionEpoch = transaction.get("time").asLong();
        Instant newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));
        newBalance.put("time", newBalanceInstant.toString());

//        System.out.println("Aggregate iteration before: " + balance + " after: " + newBalance);

        return newBalance;
    }


//    private String getCustomerDtoName(String key, String jsonValue){
//        Objects.requireNonNull(jsonValue, "jsonValue cannot be null");
//        CustomerDto customer = toCustomer(jsonValue);
//        System.out.println("selectKey selected -- " + jsonValue);
//        return customer == null ? null : customer.getName();
//    }
//
//    private CustomerDto toCustomer(String jsonValue) {
//        ObjectMapper om = new ObjectMapper();
//        CustomerDto customerDto = null;
//        try {
//            customerDto = om.readValue(jsonValue, CustomerDto.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return customerDto;
//    }



//    private KeyValue<String, String> toCustomerKeyValue(String key, String jsonValue){
////        ObjectMapper om = new ObjectMapper();
//        CustomerDto customerDto = null;
//
//        return KeyValue.pair(customerDto.getName(), jsonValue);
//    }


}
