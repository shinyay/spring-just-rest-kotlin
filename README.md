# spring-just-rest-kotlin

- [ことりんと一緒 Springもね - 5. Actuator](https://qiita.com/shinyay/items/4e1e36f2f975e37dc0ab)

## 前提 / 環境
### ランタイムバージョン
- Kotlin : 1.3.0
- SpringBoot : 2.1.0.RELEASE

### Spring Dependencies
- Web
- Actuator

### 開発環境
- OS : Mac
- IDE : IntelliJ IDEA
- Build : Gradle

## 手順 / 解説
### Spring Actuator の利用
Spring Actuator を使えるようにするには、build.gradle に次の依存を定義します。

```gradle
implementation('org.springframework.boot:spring-boot-starter-actuator')
```

これだけで使えるようになります。
あとは、次のドキュメント 「[Endpoints](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints) 」に記載のあるエンドポイントにアクセスする事で様々な情報をHTTP経由で取得することが出来るようになります。

例：HEALTH

```bash
$ curl 'http://localhost:8080/actuator/health' -i -X GET

HTTP/1.1 200
Content-Type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sun, 11 Nov 2018 13:08:00 GMT

{"status":"UP"}
```

### Spring 1.x と 2.x の差異
異なる点はいろいろありますが、1.x から 2.x にマイグレーションした時に遭遇しがちな代表的な点をいくつか見てみます。

#### アクセスベースパス
1.xでは、エンドポイントに直接アクセスして情報を表示していました。: `http://localhost:8080/health`
2.xでは、エンドポイントの前にベースパスを指定してアクセスを行います。: `http://localhost:8080/<ベースパス>/health`

デフォルトでは、ベースパスは **/actuator** です。

変更するには、設定ファイル(application.yml)に定義を行います。
以下のように定義することでベースパースを変更できます。以下の例では、ベースパスが `/admin` になっています。

```yaml
management:
  endpoints:
    web:
      base-path: /admin
```

#### 公開エンドポイント
1.xでは、エンドポイントは予め公開されていました。
2.xでは、公開されているエンドポイントは、`/health` と `/info` のみになりました。[->参考](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints-exposing-endpoints)

2.xでも、予め全て公開しておくには次のように設定ファイル(application.yml)に定義を行います。

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

例では、公開対象をアスタリクス(\*)で指定していますが、個別に指定する場合は、対象のエンドポイントを記述する事で公開できます。

```yaml
        include: beans, env
```