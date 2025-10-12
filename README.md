# Rococo

**Rococo** — это микросервисная платформа для управления художественными коллекциями и данными о музеях.  
Проект реализован в рамках дипломной работы и демонстрирует использование современных технологий.

---

## Архитектурная схема проекта
<img width="1029" height="793" alt="image" src="https://github.com/user-attachments/assets/9f5988f7-9c73-470f-9575-1342cc338a74" />

В проекте 8 микросервисов. У каждого сервиса, кроме gateway, своя mysql БД.
rococo-auth и rococo-gateway являются Rest сервисами. Сервис auth публикует события об успешной регистрации в kafka, а сервис rococo-userdata подписан на топик и сохраняет созданного пользователя у себя в БД.
Сервисы rococo-artist, rococo-museum, rococo-painting, rococo-userdata, rococo-geo являются Grpc сервисами. Фронтенд по rest взаимодействует только с auth и gateway, а уже gateway по grpc обращается в соответствующие сервисы. Также в gateway отправляются события в kafka в топик "events" при создании и обновлении картины, а сервис rococo-kafka-log слушает данный топик и логгирует в свою БД все события с картинами.

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

#### 2. Поднять в docker контейнеры с mysql, kafka и zookeeper
В корне проекта выполнить
```posh
./localenv.sh
```

#### 3. Обновить зависимости и запустить фронт Rococо (локально):
Находясь в корне проекта выполнить
```posh
cd rococo-client
npm i
npm run dev
```
Фронт будет доступен по ссылке http://127.0.0.1:3000/
#### 4. Поднять backend (локально):
Для локального запуска всех сервисов необходимо запустить их с заданием профиля local. Т.к grpc сервисы занимают сразу 2 порта, то при запуске может возникнуть ошибка "port already in use". В корне проекта есть файл ports.sh (работает на macos), который убивает все процессы работающие на портах, которые используются сервисами. Можно выполнить, если на данных портах не поднято ничего важного!
#### 5. Запуск в docker
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
Сеть rococo-net будет создана автоматически
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
