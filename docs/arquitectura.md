# Convención de capas

Todos los módulos de este repositorio siguen la misma idea hexagonal: **el dominio no
conoce a Spring ni a Kafka**. Kafka entra siempre por un adaptador, de entrada
(consumidor) o de salida (productor).

## Las tres capas

| Capa | Qué contiene | De qué puede depender |
|---|---|---|
| `domain` | Modelo, casos de uso y los puertos (`api` = entrada, `spi` = salida). Java puro. | De nada del framework. |
| `application` | Orquesta: DTOs, handlers y mappers entre el mundo exterior y el dominio. | Del dominio. |
| `infraestructure` | Adaptadores concretos: REST, JPA, Kafka, clientes HTTP y la configuración de beans. | De todo. |

La dirección de las dependencias apunta siempre **hacia dentro**: `infraestructure` →
`application` → `domain`. El dominio se instancia a mano en una clase de configuración
(`BeanConfiguration`), precisamente para que no necesite anotaciones de Spring.

## Estructura de referencia

`ProductsMicroService` y `TransferService` son los dos módulos completos y siguen esta
forma:

```
src/main/java/<paquete>/
├── domain/
│   ├── api/            # Puertos de entrada (lo que el caso de uso ofrece)
│   ├── spi/            # Puertos de salida (lo que el caso de uso necesita)
│   ├── usecase/        # Implementación de los casos de uso
│   ├── model/          # Modelo de dominio
│   └── exception/      # Excepciones de negocio
├── application/
│   ├── dto/request/    # DTOs de entrada
│   ├── handler/        # Coordinación entre el borde y el dominio
│   └── mapper/         # DTO ↔ modelo de dominio
└── infraestructure/
    ├── adapters/in/rest/    # Controladores REST y manejo de errores HTTP
    ├── adapters/out/        # Implementaciones de los puertos SPI
    │   ├── jpa/             # Persistencia (entidad, repositorio, mapper, adaptador)
    │   ├── kafka/           # Productor
    │   ├── outbox/          # Patrón Outbox: adaptador, entidad, repositorio y relay
    │   └── rest/            # Cliente HTTP hacia servicios remotos
    └── configuration/       # Wiring de los beans hexagonales
```

> **Ojo con el nombre `out`.** El paquete de adaptadores de salida se llama `out` por
> convención, y las plantillas de `.gitignore` de IntelliJ traen una regla `out/` pensada
> para el directorio de compilación del IDE. Sin las negaciones
> `!**/src/main/**/out/`, Git deja de versionar código fuente **sin avisar**. Los
> `.gitignore` de este repositorio ya las incluyen.

## Variaciones por módulo

Los módulos no son idénticos: cada uno se escribió para practicar un tema distinto y
conserva la forma que le corresponde.

| Módulo | Forma |
|---|---|
| `ProductsMicroService` | Estructura de referencia completa. Adaptador de salida Kafka (productor). |
| `TransferService` | Estructura de referencia completa, más los adaptadores de Outbox y el relay. |
| `EmailNotificationService` | Variante: los puertos viven en `application/port/{input,output}` en vez de `domain/{api,spi}`, y los adaptadores cuelgan de `infraestructure/{http,kafka,out/jpa}`. |
| `DepositService`, `WithdrawalService` | Deliberadamente mínimos: solo un `@KafkaListener` y su configuración. Su papel en el laboratorio es consumir con `read_committed`, no demostrar capas. |
| `Core`, `KafkaTransactions/core` | Sin capas: son *shared kernel*, únicamente eventos y excepciones compartidas. |

## Nota sobre `infraestructure`

El paquete se llama `infraestructure`, con la grafía española. Es intencionado y
consistente en todos los módulos; no es una errata puntual.
