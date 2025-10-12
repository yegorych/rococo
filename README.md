# Rococo

**Rococo** — это микросервисная платформа для управления художественными коллекциями и данными о музеях.  
Проект реализован в рамках дипломной работы и демонстрирует использование современных технологий.

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

graph TD
subgraph Frontend
client[Rococo Client (Svelte)]
end

subgraph Gateway
gateway[Rococo Gateway (Spring REST + gRPC)]
end

subgraph Auth
auth[Rococo Auth (Spring REST)]
end

subgraph Kafka
kafka[Kafka]
topic[Topic: events]
end

subgraph GrpcServices
artist[Rococo Artist (gRPC)]
museum[Rococo Museum (gRPC)]
painting[Rococo Painting (gRPC)]
geo[Rococo Geo (gRPC)]
userdata[Rococo Userdata (gRPC)]
kafkalog[Rococo Kafka Log]
end

subgraph Databases
db_auth[(MySQL: auth)]
db_artist[(MySQL: artist)]
db_museum[(MySQL: museum)]
db_painting[(MySQL: painting)]
db_geo[(MySQL: geo)]
db_userdata[(MySQL: userdata)]
db_kafkalog[(MySQL: kafka-log)]
end

client -->|REST| gateway
client -->|REST| auth
gateway -->|gRPC| artist
gateway -->|gRPC| museum
gateway -->|gRPC| painting
gateway -->|gRPC| geo
gateway -->|gRPC| userdata

auth -->|publish registration| kafka
kafka -->|events| userdata
kafka -->|events| kafkalog
gateway -->|publish painting events| kafka

auth --> db_auth
artist --> db_artist
museum --> db_museum
painting --> db_painting
geo --> db_geo
userdata --> db_userdata
kafkalog --> db_kafkalog
