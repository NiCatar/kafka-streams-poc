# Kafka Streams poc

## Levantar entorno

```bash
docker-compose up

# en otra terminal extraer la ip del contenedor de kafka y agregarlo en el /etc/hosts con en nombre kafka1.
docker inspect kafka1 --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
```

## Crear los topicos

```bash
docker exec -it kafka1 bash
cd bin

./kafka-topics.sh --bootstrap-server localhost:9092 --topic customers_transfer \
  --partitions 3 \
  --replication-factor 1 \
  --create
  
./kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_transaction_keyed \
  --partitions 3 \
  --replication-factor 1 \
  --create
  
./kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_balance \
  --partitions 3 \
  --replication-factor 1 \
  --create
  
./kafka-topics.sh --bootstrap-server localhost:9092 --topic customer_enrichment \
  --partitions 3 \
  --replication-factor 1 \
  --create
  
exit
```

## Consumir de los topicos

Abrir tres consolas y ejecutar los siguientes comandos en cada una

```bash
docker exec -it kafka1 bash
cd bin
```

### Terminal 1

```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic customers_transfer \
  --from-beginning
```

### Terminal 2

```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic customer_balance \
  --from-beginning \
  --property print.key=true \
  --property print.value=true \
  --property key.separator=, \
  --formatter kafka.tools.DefaultMessageFormatter \
  --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer 
```

### Terminal 3

```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic customer_enrichment \
  --from-beginning \
  --property print.key=true \
  --property print.value=true \
  --property key.separator=, \
  --formatter kafka.tools.DefaultMessageFormatter \
  --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

## Ejecutar aplicaciones

### Corremos el producer

```bash
cd customer-transaction-producer

mvn clean package && java -jar target/*s.jar
```

### Corremos los streams

```bash
cd customer-balance-streams

mvn clean package && java -jar target/*s.jar
```

## Eliminar topicos

```bash
docker exec -it kafka1 bash
cd bin
# Ver los topicos existentes
./kafka-topics.sh --bootstrap-server localhost:9092 --list

# Eliminar todos los topicos creados y de changelog
./kafka-topics.sh --bootstrap-server localhost:9092 --topic $TOPIC_TO_DELETE --delete

exit
```
