## 概要 / 説明
[ことりんと一緒 Springもね - 9. データベースマイグレーション - Flyway](https://qiita.com/shinyay/items/95934e221d7372aca394) では、Flywayを利用したデータベースレイアウトのバージョン管理の方法を確認しました。
確認の中で利用したデータベースは、作業の簡略化のために **H2 Database** を使用し、アプリケーションの起動とともに利用できるインメモリデータベースの状態で実施しました。

一方で実際にアプリケーションを稼働させる場合は、データベースサーバとして稼働するタイプをほとんどの場合利用すると思います。
そこで、今回はサーバ起動している **MySQL** に対して Flyway を使用してみます。

- [Flyway : Version control for your database](https://flywaydb.org)

![flyway-logo-tm.png](https://qiita-image-store.s3.amazonaws.com/0/127983/1ed5fa50-10d5-7c2b-3c11-5a4cace86bbf.png)


## 前提 / 環境
### ランタイムバージョン
- Kotlin : 1.3.21
- SpringBoot : 2.1.1.RELEASE

### Spring Dependencies
- Web
- JDBC
- JPA
- Actuator
- **Flyway**

### 開発環境
- OS : Mac
- IDE : IntelliJ IDEA
- Build : Gradle

## 手順 / 解説
### MySQL サーバの準備
MySQLサーバは Docker コンテナを利用して簡単に用意します、

#### docker-compose.yml の用意
次の環境が起動するようなd `ocker-compose.yml` を作成します。

|項目|内容|
|---|----|
|MySQL バージョン|5.7|
|アクセスユーザ|guest|
|アクセスパスワード|guest|

```yaml
version: "3.7"
services:
  db:
    image: mysql:5.7
    container_name: my_db
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: guest
      MYSQL_PASSWORD: guest
      MYSQL_DATABASE: guest
    restart: always
    command: --default-authentication-plugin=mysql_native_password
    ports:
      - 3306:3306
```

#### MySQL サーバの起動
`docker-compose.yml` の配置場所、またはファイルを `-f` オプションで指定して、次のコマンドで Docker コンテナとして MySQL を起動します。

```
$ docker-compose up -d
```

### Application 定義

以下のデータベース接続定義及びFlyway定義を追加します。

|項目|設定値|
|---|----|
|JDBCドライバ|com.mysql.cj.jdbc.Driver|
|データベース接続URL|jdbc:mysql://localhost:3306/app?autoReconnect=true&useSSL=false|
|アクセスユーザ|guest|
|アクセスパスワード|guest|
|Flyway ベースラインバージョン|0.0.0|
|Flyway ベースライン説明|<< Flyway Baseline >>|
|Flyway マイグレーション・スクリプトの配置場所|classpath:db/migration|

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/app?autoReconnect=true&useSSL=false
    username: guest
    password: guest
  flyway:
    enabled: true
    url: jdbc:mysql://localhost:3306/app?autoReconnect=true&useSSL=false
    user: guest
    password: guest
    baseline-on-migrate: true
    baseline-version: 0.0.0
    baseline-description: << Flyway Baseline >>
    locations: classpath:db/migration
```

### Flyway実行 (アプリケーション起動)
アプリケーションの起動時にFlywayを実行します。

#### SpringBoot 実行
以下のコマンドで SpringBoot を実行します。

```
$ ./gradlew bootRun
```

起動時ログから Flyway が正常実行されている事が確認できます。

```
[           main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 5.2.4 by Boxfuse
[           main] o.f.c.internal.database.DatabaseFactory  : Database: jdbc:mysql://localhost:3306/app (MySQL 5.7)
[           main] o.f.core.internal.command.DbValidate     : Successfully validated 2 migrations (execution time 00:00.019s)
[           main] o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table: `app`.`flyway_schema_history`
[           main] o.f.core.internal.command.DbMigrate      : Current version of schema `app`: << Empty Schema >>
[           main] o.f.core.internal.command.DbMigrate      : Migrating schema `app` to version 1.0.0 - Create-InitialTable
[           main] o.f.core.internal.command.DbMigrate      : Migrating schema `app` to version 1.1.0 - Insert-InitialData
[           main] o.f.core.internal.command.DbMigrate      : Successfully applied 2 migrations to schema `app` (execution time 00:00.109s)
```

#### MySQL 確認
MySQLにアクセスしFlywayによる変更を確認します。

```
mysql> show tables;
+-----------------------+
| Tables_in_app         |
+-----------------------+
| flyway_schema_history |
| message               |
+-----------------------+
```

```
mysql> select id,title,message from message;
+--------------------------------------+-------+--------------------+
| id                                   | title | message            |
+--------------------------------------+-------+--------------------+
| 12345678-e9d9-4d1e-ba79-01f8b8715ba9 | INIT  | Inserted by FLYWAY |
| 7b23257c-e9d9-4d1e-ba79-01f8b8715ba9 | INIT  | Inserted by FLYWAY |
+--------------------------------------+-------+--------------------+
2 rows in set (0.00 sec)
```

`message` テーブルと、Flyway の履歴管理テーブル `flyway_schema_history` が作成されている事が確認でき、
データが追加されている事も確認できました。
