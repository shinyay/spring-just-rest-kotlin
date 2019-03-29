## 前提 / 環境
### ランタイムバージョン
- Kotlin : 1.3.11
- SpringBoot : 2.1.1.RELEASE

### Spring Dependencies
- Web
- Actuator
- JDBC
- JPA

### 開発環境
- OS : Mac
- IDE : IntelliJ IDEA
- Build : Gradle
- DB : H2 Database

Repository 層を設けるに際して、今回ここでは永続化領域としてデータベースを対象にします。
また、そのデータベースの種類は簡略化のためH2データベースを使用し、組み込みモードで実行することにします。

## 概要 / 説明

![service](https://camo.qiitausercontent.com/6026a1981bf5b38209c518ba69f83786875d09b1/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e616d617a6f6e6177732e636f6d2f302f3132373938332f31366562616336382d306430302d646230342d383464642d3533383462303335336334302e706e67)

これにより、Controller 層と Service 層でそれぞれフォーカスする機能領域を分離する事ができるようになります。

|レイヤ|役割|
|-----|---|
|Controller 層|リクエストの受付け|
|Service 層|リクエスト受付け以外のビジネスロジックの処理|


さて新規に設けた、Service 層の責務について考えてみます。

Service 層の役割としては、`リクエストの受付け以外の` ビジネスロジックの処理としました。
つまり、このままでは、ビジネスロジックの結果の永続化処理もこの Sevice 層で行うことになります。

ところで、データの永続化を考えてみると、永続化する対象領域を Service 層で意識すると、
対象領域の種類に応じた設計する必要があります。

また、外部領域の種類に依存関係が強くなり、変更をする際に影響範囲が大きくなってしまいます。
例えば、データベースを例に考えると、Oracle DB から MySQL に変更をすると、その方言の違いを意識した設計が必要になります。

![service-db.png](https://qiita-image-store.s3.amazonaws.com/0/127983/06f28ab6-e56e-f249-d7a3-40358475fc92.png)

Service 層の役割である **ビジネスロジックの処理** から考えると、結果の永続化先は意識しないで、透過的に処理ができた方が設計がシンプルになり、保守性も向上します。

この Service 層から永続化処理を分離するための仕組みとして **Repository** 層を設けます。

![repository.png](https://qiita-image-store.s3.amazonaws.com/0/127983/cc0be0b1-ee99-3b1d-e241-8afd761b0202.png)

## 手順 / 解説
### Dependency
データベースアクセスを行うため、以下の Dependency を build.gradle に追加します。

- org.springframework.boot:spring-boot-starter-jdbc
- org.springframework.boot:spring-boot-starter-data-jpa

また、H2 Database を利用するための以下の Dependency を追加します。

- com.h2database:h2

### データベース定義
application.yml に以下のH2データベースに関する定義を追加します。

```yaml
spring:
  datasource:
    tomcat:
      test-on-borrow: true
      validation-interval: 30000
      validation-query: SELECT 1
      remove-abandoned: true
      remove-abandoned-timeout: 10000
      log-abandoned: true
      log-validation-errors: true
      max-age: 1800000
      max-active: 50
      max-idle: 10
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:app;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE
    username: guest
    password: guest
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Repository 層
永続化処理を行うレイヤを設けるために次のインターフェース定義を行いました。

```kotlin
interface MessageRepository : CrudRepository<Message, String>
```

このインターフェースを介してデータの永続化を行うエンティティの操作を行うようにします。

### Service 層
Service 層からは インジェクションしたRepository層のインスタンスを介して、エンティティの操作を行います。

```kotlin
@Autowired
lateinit var repository: MessageRepository

fun getMessages() : Iterable<MessageDTO> = repository.findAll().map { it -> MessageDTO(it) }

fun insertMessage(messageDto: MessageDTO) = MessageDTO(
        repository.save(Message(
                                title = messageDto.title,
                                message = messageDto.message
        ))
)
```

