# spring-just-rest-kotlin

- [ことりんと一緒 Springもね - 4. REST API デザイン](https://qiita.com/shinyay/items/119b4b02e9816edd6948)

## 前提 / 環境
### ランタイムバージョン
- Kotlin : 1.3.0
- SpringBoot : 2.1.0.RELEASE

### Spring Dependencies
- Web

### 開発環境
- OS : Mac
- IDE : IntelliJ IDEA
- Build : Gradle

## 手順 / 解説
### REST API デザイン
ここでは、まだ厳密に API デザインは考えずに最低限の方針だけ考えてみます。

#### アクセスURI
- **名詞** でアクセス
  - 動詞ではない

もともとの定義は、クラスへのアノテーションでルートパスを決め、関数毎にアクセスURIを決めていました。そこでの名称は、操作がわかりやすいように動詞表記でした。

```kotlin
@RequestMapping("/simple")
class SimpleController {

    @GetMapping(value = ["/display"])
    fun getMessages() : List<Message> {
```

そのため、実際には次のようにアクセスし、動詞的なアクセスとなっていました。

`http://xxx/simple/display`

ここの処理で *display* したいのは、**messages** でした。
そこで、上記のようなアクセスから、以下のように *messages* を対象とする意図した名詞でのアクセスに変更します。

`http://xxx/messages`

```kotlin
@RequestMapping("/messages")
class SimpleController {

    @GetMapping()
    fun getMessages() : List<Message> {
```

関数に記述していたアクセスURLがなくなっていますが、それは次で説明します。

#### HTTP メソッドと CRUD 操作
先に説明した アクセスURI で名詞でのアクセスするようにしました。
そのアクセスする名詞に対しての CRUD操作 (CREATE / READ / UPDATE / DELETE) を HTTP メソッド (GET / POST / PUT / DELETE) で表現するようにします。
そのため、先に定義していたような関数ごとの アクセスURI を必要としなくなりました。

HTTP メソッドと CRUD 操作の関係は以下のようにします。

|HTTP メソッド|CRUD 操作|意味|
|------------|--------|---|
|GET|READ|取得|
|POST|CREATE|登録|
|PUT|UPDATE|更新|
|DELETE|DELETE|削除|

##### POST / PUT - 冪等性
POST と PUT はどちらも状態の作成や更新をする際にしようする HTTP メソッドです。
では、なぜ POST を CREATE, PUT を UPDATE としたのでしょうか。

理由は HTTP メソッドの冪等性にあります。

冪等性とは簡単に説明すると再実行しても同じ結果になるような性質を言います。

HTTP メソッドの冪等性は、以下のようになります。

|HTTP メソッド|冪等性|
|------------|--------|
|GET|冪等|
|POST|非冪等|
|PUT|冪等|
|DELETE|冪等|

登録処理について想像してみてください。
同じ命令を複数回実行すると、同じレコードが複数できてしまいます。冪等ではありません。
一方で更新処理は、同じ命令は複数回実行しても結果は変わりません。冪等です。

このように考えると、**POST = CREATE = 非冪等** / **PUT = UPDATE = 冪等** が相応しいと分かります。

### REST API の修正
以上の事を踏まえて以下のように修正しました。

- 名詞アクセス
- HTTP メソッドでのCRUD処理
- 冪等性を考慮した登録処理

```kotlin:修正前
@RequestMapping("/simple")
class SimpleController {

    @PutMapping(value = ["/insert"])
    fun insertMessage(@RequestBody message: Message) : Message {}

    @PostMapping(value = ["/update"])
    fun updateMessage(@RequestBody message: Message) : Message {}
```

```kotlin:修正後
@RequestMapping("/messages")
class SimpleController {

    @PostMapping
    fun insertMessage(@RequestBody message: Message) : Message {}

    @PutMapping
    fun updateMessage(@RequestBody message: Message) : Message {}
```