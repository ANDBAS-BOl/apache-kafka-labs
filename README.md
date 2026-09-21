# Apache Kafka Labs

Laboratorios prácticos de **Apache Kafka con Spring Boot**, construidos de forma progresiva: desde un
productor/consumidor básico hasta **transacciones de Kafka** y el **patrón Outbox**.

Todos los servicios aplican **arquitectura hexagonal (puertos y adaptadores)**: el dominio no conoce
a Spring ni a Kafka. Kafka entra siempre como un *adaptador de salida* (productor) o *de entrada* (consumidor).

> Repositorio de aprendizaje y portafolio. La parte de transacciones parte del curso de Kafka de
> *appsdeveloperblog*, extendida con arquitectura hexagonal y el patrón Outbox.
>
> El **patrón Saga** vive en su propio repositorio: **[ANDBAS-BOl/saga-pattern](https://github.com/ANDBAS-BOl/saga-pattern)**

---

## Stack

`Java 21` · `Spring Boot 3.x` · `Apache Kafka (KRaft, 3 brokers)` · `Spring Data JPA` · `H2` · `Gradle` · `Docker Compose`

---

## Módulos

### 1. `Core` — contrato compartido
Librería mínima con el evento de dominio `ProductCreatedEvent`. Es el **Published Language** entre
productor y consumidor: ambos dependen del mismo contrato en lugar de duplicar la clase.

Se consume vía **composite build** de Gradle (`includeBuild '../Core'`), sin necesidad de publicar en `mavenLocal`.

### 2. `ProductsMicroService` — productor
API REST que crea productos y publica `ProductCreatedEvent` en Kafka.

**Temas que cubre:**
- Publicación **síncrona** con confirmación (`acks=all`): el endpoint no responde hasta que los 3 brokers confirman.
- **Idempotencia del productor** y `max.in.flight.requests.per.connection` para no romper el orden en reintentos.
- Ajuste de `delivery.timeout.ms`, `linger.ms` y `request.timeout.ms`.
- El puerto `IProductMessagingPort` mantiene el caso de uso ignorante de Kafka.

### 3. `EmailNotificationService` — consumidor resiliente
Consume `ProductCreatedEvent` y simula el envío de una notificación.

**Temas que cubre:**
- **Consumidor idempotente:** persiste el `messageId` ya procesado (`ProcessedEventEntity`) para que un
  reintento o un *rebalance* no dupliquen el efecto.
- **Reintentos con criterio:** `RetryableException` vs. `NotRetryableException` — un 500 del servicio
  aguas abajo se reintenta; un 404 va directo al **Dead Letter Topic**.
- Configuración explícita de `ConsumerFactory` y del *consumer group*.
- Deserialización JSON con paquetes de confianza (`trusted.packages`).

### 4. `KafkaTrasactions` — transacciones y Outbox
Operaciones bancarias (transferencia = retiro + depósito) donde **ambos eventos se publican o no se publica ninguno**.

| Servicio | Rol |
|---|---|
| `core` | Shared kernel: `WithdrawalRequestedEvent`, `DepositRequestedEvent` y excepciones comunes |
| `TransferService` | Productor **transaccional** + REST + patrón **Outbox** |
| `WithdrawalService` | Consumidor con `isolation-level=read_committed` |
| `DepositService` | Consumidor con `isolation-level=read_committed` |

**Temas que cubre:**
- **Transacciones de Kafka:** `transaction-id-prefix` y `@Transactional` para que retiro y depósito sean atómicos.
- **`read_committed`:** los consumidores nunca ven eventos de una transacción abortada.
- **Patrón Outbox:** `OutboxMessagingAdapter` escribe el evento en la tabla `outbox_events` dentro de la
  *misma transacción de base de datos* que la transferencia; `OutboxRelayScheduler` lo publica después.
  Resuelve el *dual write problem* (escribir en BD y en Kafka sin transacción distribuida).
- Validación contra un servicio remoto antes de confirmar la operación.

---

## Puesta en marcha

### 1. Levantar el clúster de Kafka
```bash
docker compose up -d
```
Arranca **3 brokers en modo KRaft** (sin ZooKeeper), con replicación 3 y `min.insync.replicas=2`.

| Acceso | Direcciones |
|---|---|
| Desde tu máquina | `localhost:9092`, `localhost:9094`, `localhost:9096` |
| Entre contenedores | `kafka-1:9090`, `kafka-2:9090`, `kafka-3:9090` |

### 2. Ejecutar un servicio
Cada módulo es un proyecto Gradle independiente con su propio wrapper:

```bash
cd ProductsMicroService && ./gradlew bootRun
cd EmailNotificationService && ./gradlew bootRun
```

> **Importante:** no muevas ni renombres las carpetas. `ProductsMicroService` y `EmailNotificationService`
> referencian `../Core`, y los servicios de `KafkaTrasactions` referencian `../core`, mediante
> *composite builds* con rutas relativas.

### 3. Servicio mock (opcional)
`EmailNotificationService` y `TransferService` validan contra un servicio HTTP en `http://localhost:8082`
que devuelve códigos a demanda (`/response/200`, `/response/500`...). Es el `mockservice` del curso de
appsdeveloperblog y no se incluye aquí; cualquier stub que responda por código de estado sirve.

---

## Documentación

- [`docs/arquitectura.md`](docs/arquitectura.md) — convención de capas hexagonales usada en todos los módulos.
