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
    subgraph Frontend
        FE[SvelteKit App]
    end

    subgraph Gateway
        GW[REST Gateway]
    end

    subgraph Auth
        AUTH[Auth Service (REST)]
    end

    subgraph gRPC
        MUSEUM[Museum Service]
        ARTIST[Artist Service]
        PAINTING[Painting Service]
        USERDATA[Userdata Service]
    end

    subgraph Kafka
        KAFKA[(Kafka Broker)]
        KLOG[Kafka Log Service]
    end

    subgraph Databases
        DBM[(MySQL - Museums)]
        DBA[(MySQL - Artists)]
        DBP[(MySQL - Paintings)]
        DBU[(MySQL - Users)]
        DBL[(MySQL - Logs)]
    end

    FE --> GW
    GW -->|REST/gRPC| MUSEUM
    GW -->|REST/gRPC| ARTIST
    GW -->|REST/gRPC| PAINTING
    GW -->|REST/gRPC| USERDATA
    GW --> AUTH

    AUTH --> KAFKA
    GW --> KAFKA
    KAFKA --> USERDATA
    KAFKA --> KLOG

    MUSEUM --> DBM
    ARTIST --> DBA
    PAINTING --> DBP
    USERDATA --> DBU
    KLOG --> DBL
