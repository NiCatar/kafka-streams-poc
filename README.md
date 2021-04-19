# Kafka Streams

```bash
kafka-topics.sh --bootstrap-server localhost:9092 --topic customers_transfer --delete
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_transaction_keyed --delete
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_balance --delete
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_enrichment --delete


kafka-topics.sh --bootstrap-server localhost:9092 --topic customer-balance-KSTREAM-AGGREGATE-STATE-STORE-0000000002-changelog --delete
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer-balance-KSTREAM-AGGREGATE-STATE-STORE-0000000002-repartition --delete
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer-balance-KSTREAM-AGGREGATE-STATE-STORE-0000000003-changelog --delete
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer-balance-KSTREAM-AGGREGATE-STATE-STORE-0000000003-repartition --delete
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer-balance-KSTREAM-AGGREGATE-STATE-STORE-0000000004-changelog --delete


kafka-topics.sh --bootstrap-server localhost:9092 --topic customers_transfer \
  --partitions 3 \
  --replication-factor 1 \
  --create
  
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_transaction_keyed \
  --partitions 3 \
  --replication-factor 1 \
  --create
  
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_balance \
  --partitions 3 \
  --replication-factor 1 \
  --create
  
kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_enrichment \
  --partitions 3 \
  --replication-factor 1 \
  --create
  

  
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic customers_transfer \
  --from-beginning 
  
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic customer_balance \
  --from-beginning \
  --property print.key=true \
  --property print.value=true \
  --property key.separator=, \
  --formatter kafka.tools.DefaultMessageFormatter \
  --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
  
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic customer_enrichment \
  --from-beginning \
  --property print.key=true \
  --property print.value=true \
  --property key.separator=, \
  --formatter kafka.tools.DefaultMessageFormatter \
  --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

