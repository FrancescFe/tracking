# Tracking

Spring Boot service that consumes [Dispatch service](https://github.com/FrancescFe/dispatch) topics and `dispatch.tracking` events from Kafka and publishes tracking updates to `tracking.status`.

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

## Topics

- `dispatch.tracking`: consumed by this service, contains multiple event types from `Dispatch`
- `tracking.status`: produced by this service with the derived tracking status

## Configuration

- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers

Default:

```text
localhost:9092
```

## Integration

This service consumes `dispatch.tracking` events produced by the `Dispatch` service.

Supported input payloads:

`DispatchPreparing`:

```json
{
  "orderId": "26b6f2b1-cc22-42f8-8285-82b8d309d1ae"
}
```

`DispatchCompleted`:

```json
{
  "orderId": "26b6f2b1-cc22-42f8-8285-82b8d309d1ae",
  "date": "2026-03-31T09:08:58.489926885Z"
}
```

Produced output payloads on `tracking.status`:

For `DispatchPreparing`:

```json
{
  "orderId": "26b6f2b1-cc22-42f8-8285-82b8d309d1ae",
  "status": "PREPARING"
}
```

For `DispatchCompleted`:

```json
{
  "orderId": "26b6f2b1-cc22-42f8-8285-82b8d309d1ae",
  "status": "COMPLETED"
}
```

## Local verification

To test end-to-end locally:

1. Start Kafka
2. Start Dispatch service
3. Start Tracking service
4. Publish an order.created event to Kafka

Consume `dispatch.tracking`:

```bash
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic dispatch.tracking \
  --formatter-property print.key=true \
  --from-beginning
```

Expected output:

```text
my-key  {"orderId":"26b6f2b1-cc22-42f8-8285-82b8d309d1ae"}
my-key  {"orderId":"26b6f2b1-cc22-42f8-8285-82b8d309d1ae","date":"2026-03-31T09:08:58.489926885Z"}
```

Consume `tracking.status`:

```bash
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic tracking.status \
  --formatter-property print.key=true \
  --from-beginning
```

Expected output:

```text
26b6f2b1-cc22-42f8-8285-82b8d309d1ae  {"orderId":"26b6f2b1-cc22-42f8-8285-82b8d309d1ae","status":"PREPARING"}
26b6f2b1-cc22-42f8-8285-82b8d309d1ae  {"orderId":"26b6f2b1-cc22-42f8-8285-82b8d309d1ae","status":"COMPLETED"}
```
