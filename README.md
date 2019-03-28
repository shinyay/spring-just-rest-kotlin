# spring-just-rest-kotlin

## 概要 / 説明
ほとんどのWebアプリケーションはデータベースを利用したデータの照会や保管を行います。
そこでデータベース接続を担うレイヤをアプリケーションに設けることを以前実施しました。
- [ことりんと一緒 Springもね - 8. リポジトリ層](https://qiita.com/shinyay/items/37eaf852dce713d0b06b)

一方でデータベースのレイアウトはアプリケーションの成長と一緒に変化していく事が一般的です。初期設計から変わらないことはまずありえません。
そこでデータベースの状態を管理しておく事が大事になってきます。

アプリケーションのソースコードはGitなどの管理ツールでバージョン管理が行われていると思います。
同じようにデーターベースの状態をバージョン管理するために使用するツール(フレームワーク)が、今回使用する **Flyway** です。

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

### Dependency 追加
まず、Gradle(`build.gradle`)またはMaven(`pom.xml`) のそれぞれの定義ファイルを編集し、Flyway の Dependency を追加します。

- Gradle(`build.gradle`)の場合

```gradle
dependencies {
	implementation('org.flywaydb:flyway-core')
}
```

- Maven(`pom.xml`)の場合

```xml
<dependency>
	<groupId>org.flywaydb</groupId>
	<artifactId>flyway-core</artifactId>
</dependency>
```

また、Spring のプロジェクトを最初に作る際に [Spring Initializr](https://start.spring.io) を利用する場合は、下図のように Dependencies の項目で `Flayway` を追加しておくことが可能です。

<img width="809" alt="flyway-initilizr.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/161b2e06-3904-cad5-c686-4f9a9b780766.png">

### データベース接続定義の記述
Flywayがマイグレーションする対象のデータベースの接続定義やFlywayの動作に関する設定を `application.yml` (または applicatation.properties) に定義します。

```yaml
  flyway:
    enabled: true
    url: jdbc:h2:mem:app;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE
    schemas: PUBLIC
    user: guest
    password: guest
    baseline-on-migrate: true
    baseline-version: 1.0.0
    baseline-description: Initial
    locations: classpath:db/migration
```

|命名|説明|
|---|---|
|enabled|Flyway の実行可否<br>デフォルト：true(実行する)|
|url|マイグレーション対象のデータベース接続文字列|
|schemas|対象のスキーマ<br>今回対象とするH2DBはデフォルトスキーマが `PUBLIC` のため指定 (ケースセンシティブのため大文字)|
|user/password|データベースの接続ユーザID/パスワード|
|baseline-on-migrate|Flywayのマイグレーションスクリプトの実行バージョンを途中から始めるか否か<br>デフォルト：false(最初のバージョンから全て実施)|
|baseline-version|baseline-on-migrate が true の場合に開始するバージョン|
|baseline-description|baseline-version で実施された場合に記録されるコメント|
|locations|マイグレーション・スクリプトの配置場所<br>`classpath:` クラスパス上の場所を指定 <br>`filepath:`ファイルシステム上のディレクトリを指定|

参考:Flyway Migrate Command[^1]

### データベース・マイグレーション・スクリプトの配置
Flywayは配置したSQLを決めたルールに基づいて自動実行しマイグレーションを実施します。とデフォルトではスクリプトの配置場所は、`src/main/resources` 配下に作成する次のディレクトリとなります。

- **db/migration**

<img width="358" alt="db-migration-dir.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/5a1f9d29-d7c0-da88-ce83-6a1235dbd156.png">

`db/migration` に配置するSQLファイルは Flyway が認識する命名規約に従うファイル名にする必要があります。
命名規約は以下のようになります。

- <**PREFIX**><**VERSION**>__<**DESCRIPTION**>.sql

|命名|説明|
|---|---|
| PREFIX |デフォルトは **V**<br>Vから始まるファイルをFlywayは走査し実行<br>`flyway.sqlMigrationPrefix` プロパティを `application.yml` に定義し変更可能|
| VERSION |ドット(.)またはアンダースコア(_)でメジャーバージョンとマイナーバージョンを分離可能<br>バージョンは1から始める必要あり|
| DESCRIPTION |説明表記用の項目<br>該当バージョンでの変更内容を簡単に表現|

例：`V1.0.0_my_first_flyway.sql`

### データベース・マイグレーション・スクリプトの作成
#### V1.0.0-テーブル作成
最初にテーブルを作成するDDLを定義します。

- db/migration/V1.0.0__Create-InitialTable.sql

```sql
CREATE TABLE message (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    message VARCHAR(255),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

```

アプリケーションを実行します。

```
$ ./gradlew clean bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.1.RELEASE)
  :
  :
2019-03-28 13:26:52.822  INFO 7128 --- [           main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 5.2.4 by Boxfuse
2019-03-28 13:26:53.118  INFO 7128 --- [           main] o.f.c.internal.database.DatabaseFactory  : Database: jdbc:h2:mem:app (H2 1.4)
2019-03-28 13:26:53.254  INFO 7128 --- [           main] o.f.core.internal.command.DbValidate     : Successfully validated 1 migration (execution time 00:00.018s)
2019-03-28 13:26:53.280  INFO 7128 --- [           main] o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table: "PUBLIC"."flyway_schema_history"
2019-03-28 13:26:53.329  INFO 7128 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "PUBLIC": << Empty Schema >>
2019-03-28 13:26:53.331  INFO 7128 --- [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "PUBLIC" to version 1.0.0 - Create-InitialTable
2019-03-28 13:26:53.358  INFO 7128 --- [           main] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema "PUBLIC" (execution time 00:00.089s)
  :
  :
```

起動時ログから、Flyway によるテーブル作成処理 `Migrating schema "PUBLIC" to version 1.0.0 - Create-InitialTable` が実施されたことが確認できます。

また、以下のようにH2DBコンソールを確認すると下記のように、Flywayの履歴テーブルに `1.0.0` の処理が記録されている事が分かります。

<img width="1128" alt="flyway_1_0_0.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/42f5e803-e05f-7780-cb26-e1cb6ae471f2.png">

#### V1.1.0-データ追加
次に作成したテーブルに対してデータを追加するSQLを定義します。

- db/migration/V1.1.0__Insert-InitialData.sql

```sql
INSERT INTO message(id, title, message) VALUES ('7b23257c-e9d9-4d1e-ba79-01f8b8715ba9', 'INIT', 'Inserted by FLYWAY');

INSERT INTO message(id, title, message) VALUES ('12345678-e9d9-4d1e-ba79-01f8b8715ba9', 'INIT', 'Inserted by FLYWAY');

```

先程と同様にアプリケーションを実行します。

```
$ ./gradlew clean bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.1.RELEASE)
  :
  :
2019-03-28 13:43:30.226  INFO 7430 --- [           main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 5.2.4 by Boxfuse
2019-03-28 13:43:30.431  INFO 7430 --- [           main] o.f.c.internal.database.DatabaseFactory  : Database: jdbc:h2:mem:app (H2 1.4)
2019-03-28 13:43:30.588  INFO 7430 --- [           main] o.f.core.internal.command.DbValidate     : Successfully validated 2 migrations (execution time 00:00.029s)
2019-03-28 13:43:30.617  INFO 7430 --- [           main] o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table: "PUBLIC"."flyway_schema_history"
2019-03-28 13:43:30.658  INFO 7430 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "PUBLIC": << Empty Schema >>
2019-03-28 13:43:30.659  INFO 7430 --- [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "PUBLIC" to version 1.0.0 - Create-InitialTable
2019-03-28 13:43:30.694  INFO 7430 --- [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "PUBLIC" to version 1.1.0 - Insert-InitialData
2019-03-28 13:43:30.713  INFO 7430 --- [           main] o.f.core.internal.command.DbMigrate      : Successfully applied 2 migrations to schema "PUBLIC" (execution time 00:00.107s)
```

起動時ログから、追加したマイグレーションスクリプトが実行されている事が確認できます。
- `Migrating schema "PUBLIC" to version 1.0.0 - Create-InitialTable`
- `Migrating schema "PUBLIC" to version 1.1.0 - Insert-InitialData`

H2DBコンソールを確認すると、履歴テーブルに2つのマイグレーション処理が実行された事が記録されています。

<img width="1121" alt="flyway_1_1_0.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/1816d9ed-f241-0f11-5d42-8ff3d23741dc.png">

また、作成した `MESSAGE` テーブルを照会するとマイグレーション・スクリプトとして定義していたSQLが反映され、データが追加されている事が確認できます。

<img width="914" alt="flyway_1_1_0-select-data.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/54828652-3b31-05e1-832b-f64cea82e9e9.png">

## 内容
- [REST API - RequestMapping](https://github.com/shinyay/spring-just-rest-kotlin/tree/first-rest-get)
- [REST API - GET/POST/PUT/DELETE Mapping](https://github.com/shinyay/spring-just-rest-kotlin/tree/first-rest-methods)
- [REST API デザイン](https://github.com/shinyay/spring-just-rest-kotlin/tree/first-rest-design)
- [アプリケーション監視 - Actuator](https://github.com/shinyay/spring-just-rest-kotlin/tree/first-actuator)
- [非同期処理](https://github.com/shinyay/spring-just-rest-kotlin/tree/first-async)
- [サービス](https://github.com/shinyay/spring-just-rest-kotlin/tree/first-service-layer)
- [リポジトリ](https://github.com/shinyay/spring-just-rest-kotlin/tree/first-repository-layer)