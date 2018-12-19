# spring-just-rest-kotlin

- [ことりんと一緒 Springもね - 6. 非同期処理](https://qiita.com/shinyay/items/5ad1e60435dfa9818c27)

## 概要 / 説明
従来、アプリケーションの処理は実行を行い結果を受け取るという同期的な動き方で多くのところで充分でした。
ところが近年はスマートフォンなどのモバイル機器やプロセッサのマルチコア化などの進歩に伴い、
複数の処理を同時に行えるような非同期処理も求められるようになってきています。

Spring では、`@Async` アノテーションを使用して簡単に別スレッド上での非同期処理を行えます。
`@Async` を利用した非同期処理の作り方を確認してみます。

### 非同期処理
Java で何らかの命令を実行する場合、スレッドの中で処理が行われます。
シングルスレッドの場合は、命令をスレッド上で逐次順番に処理を行っていきます。
そのため、同時処理はできません。

そこで、複数のスレッドを用意して各スレッド上で命令を実行し、
終了を待たずに別のスレッドで命令を実行して非同期的に並行処理を実現します。

![Threads.png](https://qiita-image-store.s3.amazonaws.com/0/127983/ac09fecf-492e-6d7b-cf63-6f9b590a1820.png)

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

### シンプルな非同期処理
#### 非同期処理の有効化
Spring Boot アプリケーションの中で非同期処理を利用できるようにする設定を行います。
設定内容は、コンフィグレーションクラス (`@Configuration`アノテーション、または`@SpringBootApplication` アノテーションの付与されたクラス)に `@EnableAsync` アノテーションを追加するのみです。

```kotlin
@SpringBootApplication
@EnableAsync
class SimpleApplication
```

#### 非同期処理の実装
別スレッドをの中で非同期処理を実施したい関数に対して `@Async` アノテーションを付与します。
この`@Async` アノテーションが付与された関数が非同期処理対象の関数として扱われ、処理される命令が別スレッドの中で実行されます。

```kotlin
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service("Async Task Service")
class AsyncTaskService {

    val logger = LoggerFactory.getLogger(this::class.java.name)

    @Async
    fun standardTask() {
        logger.info("Task Start")
        TimeUnit.SECONDS.sleep(5)
        logger.info("Task End")
    }
}
```

#### 非同期処理の呼び出し
`@Async` アノテーションにより非同期処理を担当するクラスを呼び出す REST コントローラを配置します。
アクセスエンドポイントは、`/async` として設定します。
これによりこの非同期処理は、`http://localhost:8080/async` で呼び出す事ができます。

```kotlin
@RestController
@RequestMapping("/async")
class AsyncTaskController {

    @GetMapping
    fun callStandardTask() = service.standardTask()
}
```

#### 非同期処理の実行
実際に非同期処理を5回連続で呼び出してみます。

```
$ curl  http://localhost:8080/async
$ curl  http://localhost:8080/async
$ curl  http://localhost:8080/async
$ curl  http://localhost:8080/async
$ curl  http://localhost:8080/async
```

下記のように各非同期処理の開始と終了の実行時ログが標準出力に表示されます。

```
2018-12-19 20:50:22.662  INFO 22121 --- [cTaskExecutor-1] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 20:51:00.545  INFO 22121 --- [cTaskExecutor-2] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 20:51:01.886  INFO 22121 --- [cTaskExecutor-3] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 20:51:04.397  INFO 22121 --- [cTaskExecutor-4] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 20:51:05.550  INFO 22121 --- [cTaskExecutor-5] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 20:51:06.887  INFO 22121 --- [cTaskExecutor-1] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 20:51:09.400  INFO 22121 --- [cTaskExecutor-2] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 20:52:03.338  INFO 22121 --- [cTaskExecutor-3] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 20:52:05.123  INFO 22121 --- [cTaskExecutor-4] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 20:52:06.290  INFO 22121 --- [cTaskExecutor-5] i.p.s.simple.service.AsyncTaskService    : Normal Task End
```

この結果を見ると、 `cTaskExecutor-1` から `cTaskExecutor-5` までの 5スレッドが生成され処理され、それぞれ非同期に処理されている事が確認できます。

### スレッドプールを利用した非同期処理
先に実施した非同期処理では、呼び出し分だけスレッドが生成されました。
もし、これが非常に高頻度で呼び出しが行われるアプリケーションだったらどうなるでしょうか。
スレッドが多数生成され、システムリソースを枯渇させてパフォーマンスの低下や、最悪の場合システムダウンという事も考えられます。
Java EE アプリケーションサーバであれば、スレッド数の制御をプラットフォームとして制御する機能を提供している製品が多数あります。
では、今回のようなJava EE アプリケーションサーバを利用しない Spring Boot アプリケーションの場合はどうしたらよいでしょうか。

#### ThreadPoolTaskExecutor の定義
Spring で非同期処理を実施する場合、`@Async` アノテーションを付与する事でデフォルトでは `SimpleAsyncTaskExecutor` が使用され別スレッドを生成し非同期処理が行われています。

一方で、スレッド数の制御を行うためにスレッドプールの設定を行いたい場合は、`ThreadPoolTaskExecutor` を構成して使用します。
`@EnableAsync` を付与したコンフィグレーションクラス内に定義を行います。

以下では、二種類のプールを作成しています。

```kotlin
@SpringBootApplication
@EnableAsync
class SimpleApplication {

    @Bean
    fun normalTaskExecutor(): TaskExecutor  = ThreadPoolTaskExecutor().apply {
        corePoolSize = 1
        setQueueCapacity(5)
        maxPoolSize = 1
        setThreadNamePrefix("NormalThread-")
        setWaitForTasksToCompleteOnShutdown(true)
    }

    @Bean
    fun prioritizedTaskExecutor(): TaskExecutor  = ThreadPoolTaskExecutor().apply {
        corePoolSize = 5
        setQueueCapacity(5)
        maxPoolSize = 5
        setThreadNamePrefix("PrioritizedThread-")
        setWaitForTasksToCompleteOnShutdown(true)
    }
}
```

以下の属性を設定してスレッドプールの構成を行います。

|名前|内容|
|---|---|
|corePoolSize|この設定値までスレッド数を作成|
|setQueueCapacity| corePoolSize 数を超えると、この設定値までキューイング|
|maxPoolSize| setQueueCapacity の最大までキューイングすると、この設定値までスレッド数を作成|

![ThreadPool.png](https://qiita-image-store.s3.amazonaws.com/0/127983/852f0a4e-0ec8-bdb1-3f31-6e90c40660b3.png)

#### ThreadPoolTaskExecutor を指定した非同期処理の実装
以下のように `@Async` アノテーションで `ThreadPoolTaskExecutor` の Bean名を明示的に指定し、
どのThreadPoolTaskExecutorによる非同期処理化を決定します。

```kotlin
    @Async("prioritizedTaskExecutor")
    fun prioritizedTask() {
        logger.info("Prioritized Task Start")
        TimeUnit.SECONDS.sleep(5)
        logger.info("Prioritized Task End")
    }

    @Async("normalTaskExecutor")
    fun normalTask() {
        logger.info("Normal Task Start")
        TimeUnit.SECONDS.sleep(5)
        logger.info("Normal Task End")
    }
```

#### 非同期処理の実行
スレッドプール数を `1` に設定している非同期処理の結果を見てみます。

```
$ curl  http://localhost:8080/async/normal
$ curl  http://localhost:8080/async/normal
$ curl  http://localhost:8080/async/normal
$ curl  http://localhost:8080/async/normal
$ curl  http://localhost:8080/async/normal
```

以下のように、`NormalThread-1` スレッドのみで処理が行われている事が分かります。
指定した通り、スレッド数 `1` で処理されています。

```
2018-12-19 22:30:17.654  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 22:30:22.658  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 22:30:24.437  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 22:30:29.441  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 22:30:29.441  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 22:30:34.442  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 22:30:34.443  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 22:30:39.445  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task End
2018-12-19 22:30:39.446  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task Start
2018-12-19 22:30:44.455  INFO 22746 --- [ NormalThread-1] i.p.s.simple.service.AsyncTaskService    : Normal Task End
```

次に、スレッドプール数を `5` にしている非同期処理を見てみます。

```
$ curl  http://localhost:8080/async/high
$ curl  http://localhost:8080/async/high
$ curl  http://localhost:8080/async/high
$ curl  http://localhost:8080/async/high
$ curl  http://localhost:8080/async/high
```

こちらは、`HighThread-1` から `HighThread-5` までの 5スレッドが生成され使用されている事が分かります。
このように簡単にスレッドプールの制御も考慮した非同期処理ができる事が分かります。

```
2018-12-19 22:37:55.784  INFO 22746 --- [HighThread-1] i.p.s.simple.service.AsyncTaskService    : Prioritized Task Start
2018-12-19 22:37:57.469  INFO 22746 --- [HighThread-2] i.p.s.simple.service.AsyncTaskService    : Prioritized Task Start
2018-12-19 22:37:57.898  INFO 22746 --- [HighThread-3] i.p.s.simple.service.AsyncTaskService    : Prioritized Task Start
2018-12-19 22:37:58.956  INFO 22746 --- [HighThread-4] i.p.s.simple.service.AsyncTaskService    : Prioritized Task Start
2018-12-19 22:37:59.582  INFO 22746 --- [HighThread-5] i.p.s.simple.service.AsyncTaskService    : Prioritized Task Start
2018-12-19 22:38:00.787  INFO 22746 --- [HighThread-1] i.p.s.simple.service.AsyncTaskService    : Prioritized Task End
2018-12-19 22:38:02.473  INFO 22746 --- [HighThread-2] i.p.s.simple.service.AsyncTaskService    : Prioritized Task End
2018-12-19 22:38:02.900  INFO 22746 --- [HighThread-3] i.p.s.simple.service.AsyncTaskService    : Prioritized Task End
2018-12-19 22:38:03.957  INFO 22746 --- [HighThread-4] i.p.s.simple.service.AsyncTaskService    : Prioritized Task End
2018-12-19 22:38:04.586  INFO 22746 --- [HighThread-5] i.p.s.simple.service.AsyncTaskService    : Prioritized Task End
```