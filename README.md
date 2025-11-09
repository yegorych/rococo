# Rococo

**Rococo** — это микросервисная платформа для управления художественными коллекциями и данными о музеях.  
Проект реализован в рамках дипломной работы и демонстрирует использование современных технологий.

---

## Архитектурная схема проекта
<img width="1029" height="793" alt="image" src="https://github.com/user-attachments/assets/9f5988f7-9c73-470f-9575-1342cc338a74" />

- **8 микросервисов**, каждый (кроме `gateway`) со своей MySQL-базой
- **REST-сервисы**: `rococo-auth`, `rococo-gateway`
- **gRPC-сервисы**: `rococo-artist`, `rococo-museum`, `rococo-painting`, `rococo-userdata`, `rococo-geo`
- **Kafka-события**:
    - `rococo-auth` публикует события регистрации в топик `users`
    - `rococo-userdata` слушает топик `users` и сохраняет пользователей в свою бд
    - `rococo-gateway` публикует события о создании/обновлении картин в топик `events`
    - `rococo-kafka-log` логгирует события из топика `events`
- **Фронтенд** (`rococo-client`) взаимодействует по REST только с `auth` и `gateway`

## Технологии
- Backend: Java 21, Spring Boot 3, Spring Data JPA, gRPC, REST, Retrofit
- Messaging: Apache Kafka
- Databases: MySQL
- Frontend: SvelteKit
- Tests: JUnit 5, Selenide, Allure, Selenoid
- Infrastructure: Docker, Docker Compose


## Как запустить?
Примечание: Все инструкции написаны для macos. Для запуска sh скриптов на windows необходимо использовать bash.
#### 1. Спулить образы mysql, kafka, zookeeper
```posh
docker pull mysql:8.0
docker pull confluentinc/cp-zookeeper:7.3.2
docker pull confluentinc/cp-kafka:7.3.2
```
#### 2. Создать docker network (bridge)
```posh
docker network create --driver bridge rococo-net
```
#### 3. Поднять в docker контейнеры с mysql, kafka и zookeeper
В корне проекта выполнить
```posh
./localenv.sh
```
#### 4. Обновить зависимости и запустить фронт Rococо (локально):
Находясь в корне проекта выполнить
```posh
cd rococo-client
npm i
npm run dev
```
Фронт будет доступен по ссылке http://127.0.0.1:3000/
#### 5. Поднять backend (локально):
Для локального запуска всех сервисов необходимо запустить их с заданием профиля local. Т.к grpc сервисы занимают сразу 2 порта, то при запуске может возникнуть ошибка "port already in use". В корне проекта есть файл ports.sh (работает на macos), который убивает все процессы работающие на портах, которые используются сервисами. Можно выполнить, если на данных портах не поднято ничего важного!
## Запуск в docker
Для поднятия backend и frontend в docker необходимо выполнить следующее:
1) Для корректного резолва доменных имен контейнеров необходимо добавить в файл hosts (на macos лежит /etc/hosts):
```posh
127.0.0.1       frontend.rococo.dc
127.0.0.1       auth.rococo.dc
127.0.0.1       gateway.rococo.dc
127.0.0.1       allure
```
2) Создать volumes
```posh
docker volume create mysqldata
docker volume create allure-results
```
3) В корне проекта выполнить
```posh
./docker-compose-dev.sh
```
Фронтенд будет по доступен по ссылке http://frontend.rococo.dc/

При выполнении docker-compose-dev.sh c аргументом 'push' все сервисы пересоберутся и запушатся в docker hub пользователя, указанного в IMAGE_PREFIX в docker.properties и в корневом build.gradle в dockerHubName
## Запуск тестов в docker
В корне проекта выполнить
```posh
./docker-compose-e2e.sh
```
Помимо всех backend сервисов поднимутся контейнеры с selenoid, allure и сам контейнер с тестами rococo-e-2-e. Selenoid ui будет доступен по урлу http://127.0.0.1:9097/, а allure ui с отчетом о пройденных тестах - http://allure:5252/.
При выполнении docker-compose-e2e.sh c аргументом 'firefox' спулится соответствующий образ для selenoid и тесты будут выполняться на firefox, иначе- chrome.

## CI/CD
При каждом Pull request пересобираются все сервисы и запускаются тесты. После прогона в комменте pr будет отображен результат выполнения тестов и ссылка на allure отчет.

## Сертификат
<img width="3000" height="1720" alt="IMG_8908" src="https://github.com/user-attachments/assets/6e7ba28a-1ca8-4bd4-a5af-b7d4a9fd1538" />
<img width="3000" height="1720" alt="IMG_8913" src="https://github.com/user-attachments/assets/36f4b7f4-5298-47a9-89cb-7f536ac9b804" />



