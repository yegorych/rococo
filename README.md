# Rococo

**Rococo** — это микросервисная платформа для управления художественными коллекциями и данными о музеях.  
Проект реализован в рамках дипломной работы и демонстрирует использование современных технологий: gRPC, REST Gateway, Kafka, JWT-авторизация и SvelteKit фронтенд.

---

## Архитектура

- **Frontend:** [SvelteKit](https://kit.svelte.dev/) (общается с Gateway по REST API).
- **Gateway:** Spring Boot сервис, принимающий REST-запросы и транслирующий их в gRPC вызовы к backend-сервисам.  
  Также публикует события в Kafka.
- **Auth Service:** REST-сервис (Spring Boot).  
  - Авторизация и аутентификация пользователей.  
  - Генерация и валидация JWT.  
  - Публикация событий регистрации/логина в Kafka.  
- **Userdata Service:** gRPC-сервис, хранит данные пользователей. Подписывается на события Kafka (регистрация, обновления).
- **Domain gRPC сервисы:**  
  - `museum-service` — управление музеями  
  - `artist-service` — управление художниками  
  - `painting-service` — управление картинами  
  У каждого своя **MySQL база данных**.
- **Kafka Log Service:** слушает события из Kafka и пишет их в свою базу для аудита и логирования.
- **Базы данных:** каждый сервис использует отдельную MySQL базу (pattern "Database per service").

---

## Архитектурная схема

```mermaid
flowchart LR
    subgraph FRONTEND
        UI[SvelteKit Frontend]
    end

    subgraph GATEWAY[API Gateway (REST <-> gRPC)]
    end

    subgraph AUTH[Auth Service (REST)]
        AUTH_DB[(MySQL Auth DB)]
    end

    subgraph SERVICES
        USERDATA[UserData Service (gRPC)]
        ARTIST[Artist Service (gRPC)]
        OTHER[Other gRPC Services...]
    end

    subgraph DBs
        USERDATA_DB[(MySQL UserData DB)]
        ARTIST_DB[(MySQL Artist DB)]
        OTHER_DB[(MySQL Other DBs)]
    end

    subgraph KAFKA[Kafka Broker]
        TOPIC_AUTH[Auth Events]
        TOPIC_GATEWAY[Gateway Events]
    end

    subgraph LOG_SERVICE[Kafka Log Listener]
        LOG_DB[(MySQL Logs DB)]
    end

    %% Connections
    UI -->|REST| GATEWAY
    GATEWAY -->|REST| AUTH
    GATEWAY -->|gRPC| USERDATA
    GATEWAY -->|gRPC| ARTIST
    GATEWAY -->|gRPC| OTHER

    AUTH -->|MySQL| AUTH_DB
    USERDATA -->|MySQL| USERDATA_DB
    ARTIST -->|MySQL| ARTIST_DB
    OTHER -->|MySQL| OTHER_DB

    AUTH -->|Publish Events| TOPIC_AUTH
    GATEWAY -->|Publish Events| TOPIC_GATEWAY

    LOG_SERVICE -->|Consume| TOPIC_AUTH
    LOG_SERVICE -->|Consume| TOPIC_GATEWAY
    LOG_SERVICE -->|MySQL| LOG_DB

    MUSEUM --> DBM
    ARTIST --> DBA
    PAINTING --> DBP
    USERDATA --> DBU
    KLOG --> DBL
```
