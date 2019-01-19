# spring-just-rest-kotlin

- [ことりんと一緒 Springもね - 7. サービス](https://qiita.com/shinyay/items/8f54c2177193b978fba2)

![service-layer.png](https://qiita-image-store.s3.amazonaws.com/0/127983/16ebac68-0d00-db04-84dd-5384b0353c40.png)

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

### 変更前のコントローラクラス

元々は以下のようにコントローラクラスの中でデータの生成など、リクエスト/レスポンスの処理以外のことを行っていました。

```kotlin
@RestController
@RequestMapping("/messages")
class SimpleController {

    @GetMapping
    fun getMessages() : List<Message> {
        return listOf(
                Message(
                        UUID.randomUUID().toString(),
                        "First Message",
                        "This is a 1st message on ${getDate()}."
                ),
                Message(UUID.randomUUID().toString(),
                        "Second Message",
                        "This is a 2nd message on ${getDate()}."
                )
        )
    }

    private fun getDate() : String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return simpleDateFormat.format(Date())
    }
}
```

### サービスクラス
元々コントローラクラスに実装していた内容を以下のようにサービスクラスに移します。

```kotlin
@Service("Message Service")
class MessageService {

    fun getMessages() : List<Message> {
        return listOf(
                Message(
                        UUID.randomUUID().toString(),
                        "First Message",
                        "This is a 1st message on ${getDate()}."
                ),
                Message(UUID.randomUUID().toString(),
                        "Second Message",
                        "This is a 2nd message on ${getDate()}."
                )
        )
    }

    private fun getDate() : String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return simpleDateFormat.format(Date())
    }
}
```

サービスクラスには `@Service` アノテーションを付与します。
これによりコントローラクラスの中で Dependency Injection により利用できるようになります。

```kotlin
@Autowired
private lateinit var service: MessageService
```

`lateinit` キーワードは、インスタンスを初期化せずに宣言するときに使用します。

### 変更後のコントローラクラス
サービスクラスを使用するコントローラクラスでは、リクエスト/レスポンスに関する処理だけを担当するようにします。
以下のようにサービスクラスで実装している処理を呼び出すように変更を行います。

```kotlin
@RestController
@RequestMapping("/messages")
class MessageController() {

    @Autowired
    private lateinit var service: MessageService

    @GetMapping
    fun getMessages() = service.getMessages()
}
```
