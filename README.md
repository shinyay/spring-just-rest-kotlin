# spring-just-rest-kotlin

- []()

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
### 概要
簡単な以下の処理を追加してみます。

- REST アクセスのためのコントローラの追加: **RestController**
- アクセス時に表示する情報のためのエンティティの追加: **Data Class**

#### RestController
RestController クラスを定義するために **@RestController** アノテーションを付けたクラスを作成します。

```kotlin:RestController
@RestController
class SimpleController { 
  ...
}
```
ところで、この @RestController は、**@Controller** と **@ResponseBody** を組み合わせたものです。
そのため、次のように定義していても同様の振る舞いをします。

```kotlin:Controller
@Controller
@ResponseBody
class SimpleController { 
  ...
}
```

##### RequestMapping
**@RequestMapping** アノテーションを付けて、クライアントからのリクエストに対してマッピングを行います。

以下の属性を使ってマッピング条件を指定します。

- value
    - URL のパスを記述
- method
    - GET や POST などのメソッドを指定
- headers
    - HTTP のヘッダを指定
- params
    - リクエストパラメーターを指定

##### GetMapping
**@GetMapping** アノテーションは、`@RequestMapping(method = RequestMethod.GET)` のショートカットとして機能するアノテーションです。

#### Data Class
データクラスは、処理は行わずデータだけを保持するために使用するクラスです。
データクラスは以下の書式で定義します。

- **data** キーワードを class の前に追加
- プライマリーコンストラクタを定義

```kotlin:DataClass
data class Data(var id: String, var value: String)
```

データクラスと定義した内容から、以下の処理をコンパイラが自動で推論し生成します。

- equals() / hashCode()
- "Data(id=foo, value=bar)" 形式の toString()
- copy()
- 宣言した順番で内容を取り出す componentN()


### Data Class
```kotlin
data class Message(var id: String,
                   var title: String,
                   var message: String)
```

Java で JavaBeans を定義を定義するときに記述していた getter, setter, toString, equals がなくシンプルな記述内容になっているのが見て分かると思います。
Java でも **Lombok** を使用する事で同様の記述を実現していましたが、Kotlin では言語レベルでこの書式をサポートしています。

### RestController
```kotlin
import io.pivotal.syanagihara.simple.data.Message
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/simple")
class SimpleController {

    @GetMapping
    fun getMessages() : List<Message> {
        return listOf(
                Message(
                        UUID.randomUUID().toString(),
                        "First Message",
                        "This is a 1st message on ${getDate()}."
                )
        )
    }

    private fun getDate() : String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return simpleDateFormat.format(Date())
    }
}
```

#### Java API の利用
現在日時を出力するために、**java.util.Date** クラスを使っています。
このように Kotlin から Java の API 呼び出しは透過的に行う事ができます。

#### アクセス修飾子
getDate() を private 宣言していますが、簡単に Kotlin のアクセス修飾子についてまとめます。

| アクセス修飾子 | 内容 |
|:--|:--|
| public | 全てのクラスからアクセス可能 |
| internal | 同じモジュール内のクラスからアクセス可能 |
| protected | サブクラスからのみアクセス可能 |
| private | 宣言したクラスからのみアクセス可能 |