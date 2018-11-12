# spring-just-rest-kotlin

- [ことりんと一緒 Springもね - 3. 関数から波括弧省略](https://qiita.com/shinyay/items/4152259d73de02d5e9c0)

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

### その他
- APIデザイン
    - 分かりやすくするため、きちんとしたAPIデザインはしてません。GET/PUT/POST/DELETEのそれぞれのAPIに、それぞれのアクセスパスを付与しています。

## 手順 / 解説

以前に GET アクセス用に用意した API では、以下のように`@GetMapping` アノテーションを使いました。

```kotlin
@GetMapping(value = ["/display"])
fun getMessages() : List<Message> {...}
```

PUT/POST/DELETE も同様にそれぞれ `@PutMapping` / `@PostMapping` / `@DeleteMapping` を使って定義します。

```kotlin
@PutMapping(value = ["/insert"])
fun insertMessage(@RequestBody message: Message) : Message {...}

@PostMapping(value = ["/update"])
fun updateMessage(@RequestBody message: Message) : Message {...}

@DeleteMapping(value = ["/delete/{id}"])
fun deleteMessage(@PathVariable(name = "id") id: String): Boolean = {...}
```

### 関数から波括弧の省略
通常、関数を定義するときは `{...}` **波括弧** で囲み、その中に処理する式を記述します。
Kotlinでは、その式がただ1つの場合、波括弧を省略して記述する事ができます。
よって、次の2つの関数は同じ意味となります。

```kotlin:波括弧あり
@DeleteMapping(value = ["/delete/{id}"])
fun deleteMessage(@PathVariable(name = "id") id: String): Boolean {
  return true
}
```

```kotlin:波括弧省略
@DeleteMapping(value = ["/delete/{id}"])
fun deleteMessage(@PathVariable(name = "id") id: String): Boolean = true
```