# Tracking

Spring Boot service that consumes `dispatch.tracking` events from Kafka.

## Requirements

- Java 21
- Kafka running on `localhost:9092` for local execution

## Run

```bash
./gradlew bootRun
```

## Build

```bash
./gradlew build
```

## Topic

- `dispatch.tracking`: consumed by this service

## Configuration

- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers

Default:

```text
localhost:9092
```

## Integration

This service consumes `dispatch.tracking` events produced by the `Dispatch` service.

Expected event payload:

```json
{
  "orderId": "26b6f2b1-cc22-42f8-8285-82b8d309d1ae",
  "status": "DISPATCHED"
}
```

To test end-to-end locally:
1. Start Kafka
2. Start Dispatch service
3. Start Tracking service
4. Publish an order.created event to Kafka
