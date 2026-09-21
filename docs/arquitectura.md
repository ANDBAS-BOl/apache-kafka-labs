```

/

├── post-get-personas/      # HU 1–6: Java REST API (formerly main-api/)

│   ├── src/

│   │   ├── domain/ 

│   │   │   ├── api/                            # Puertos de entrada (use cases)

│   │   │   ├── spi/                            # Puertos de salida (persistencia, APIs externas)

│   │   │   ├── usecase/                        # Implementación de casos de uso

│   │   │   ├── model/                          # Entidades / value objects de dominio

│   │   │   ├── exception/                      # Excepciones de negocio

│   │   │   └── utils/                          # Constantes/mensajes de dominio

│   │   ├── application/

│   │   │   │   ├── dto/

│   │   │   │   │   ├── request/                    # DTOs HTTP de entrada

│   │   │   │   │   └── response/                   # DTOs HTTP de salida

│   │   │   │   ├── handler/                        # Interfaces de handlers

│   │   │   │   │   └── impl/                       # Implementaciones (@Component)

│   │   │   │   └── mapper/                         # MapStruct: DTO ↔ Model

│   │   └── infrastructure/

│           ├── configuration/                  # @Configuration, wiring de beans hexagonales

│           ├── exceptionhandler/               # @ControllerAdvice, HTTP ↔ excepciones dominio

│           ├── input/

│           │   └── rest/                       # @RestController

│           ├── out/

│               ├── jpa/

│               │   ├── adapter/                # Implementa \*PersistencePort (spi)

│               │   ├── entity/                  # @Entity JPA

│               │   ├── mapper/                 # MapStruct: Entity ↔ Model

│               │   └── repository/             # Spring Data JPA

│               ├── http/

│                    ├── adapter/                # Implementa puertos spi HTTP

│                    └── client/                 # Feign/RestTemplate/WebClient + DTOs externos

```

