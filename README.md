# spring-just-boot-kotlin

- [Qiita](https://qiita.com/shinyay/items/ee64ab3ad91b90ed4621)

## 前提 / 環境
### ランタイムバージョン
- Kotlin : **1.3.0**
- SpringBoot : 2.1.0.RELEASE

ちなみに、SPRING INITIALIZR で生成した状態では、デフォルトの Kotlin バージョンは、**1.2.70** (as of 2018/11/1) でした。

### Spring Dependencies
- Web

### 開発環境
- OS : Mac
- IDE : IntelliJ IDEA
- Build : Gradle

## 手順 / 解説

[SPRING INITIALIZR](https://start.spring.io) で生成した雛形を IntelliJ にインポートして使用します。
前述したとおり、Kotlin のバージョンは**1.2.70** から **1.3.0** に変更しています。

### SPRING INITIALIZR

[SPRING INITIALIZR](https://start.spring.io) は、文字通り Spring プロジェクトの雛形を作り簡単に始められるようにするためのオンラインツールです。
操作は非常にシンプルで以下の項目を画面上から選択するみです。

- プロジェクトタイプ : [ Maven / Gradle]
- 開発言語 : [Java / Kotlin / Groovy]
- Spring Boot バージョン : <img width="168" alt="Spring Initializr 2018-11-02 00-13-42.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/da4999bc-d61c-360b-6f69-898595e990d9.png">
- Group : ルートパッケージ名
- Artifact : プロジェクト名
- Dependencies : 依存コンポーネント
  - 今回は **Web** を追加

<img width="1280" alt="Spring Initializr 2018-11-02 00-03-21.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/8fe8b17f-2f2e-e1c9-07e9-4d1a81344fb8.png">

Java/Kotlin ともに **Generate Project** をクリックすると、Spring プロジェクトの雛形が生成されてダウンロードできます。
その雛形を IDE にインポートして使用します。

#### 今回の Java プロジェクト

<img width="1280" alt="Spring Initializr 2018-11-01 21-50-43.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/1d0ec67e-4eea-64b1-b990-128358374cec.png">

#### 今回の Kotlin プロジェクト

<img width="1280" alt="Spring Initializr 2018-11-01 21-51-57.png" src="https://qiita-image-store.s3.amazonaws.com/0/127983/03d42c61-0c15-a2e2-9ef0-8e4f0db01060.png">



### build.gradle の比較

以下に記述している Java プロジェクト用の build.gradle と Kotlin プロジェクト用の build.gradle を比較してみます。

当然ですが基本的には両者の記述内容や書式は同じな事が分かります。
異なる点は、次の２点です。

- 適用プラグイン
- 依存ライブラリ

プラグインは、Kotlin プロジェクトでは Java プラグインの代わりに Kotlin プラグインを適用しています。

```gradle
apply plugin: 'kotlin'
apply plugin: 'kotlin-spring'
```

同様に依存ライブラリについても、Kotlinのライブラリを追加しています。

```gradle
implementation('com.fasterxml.jackson.module:jackson-module-kotlin')
implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
implementation("org.jetbrains.kotlin:kotlin-reflect")
```

追加しているスタンダードライブラリが *jre* でなく *jdk* になっているのは、また別の話で。
いずれにしても、**jdk** を指定してください。

#### Java

```gradle
buildscript {
	ext {
		springBootVersion = '2.1.0.RELEASE'
	}
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
	}
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group = 'io.pivotal.syanagihara'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = 1.8

repositories {
	mavenCentral()
}


dependencies {
	implementation('org.springframework.boot:spring-boot-starter-web')
	testImplementation('org.springframework.boot:spring-boot-starter-test')
}
```

#### Kotlin

```gradle
buildscript {
	ext {
		kotlinVersion = '1.3.0'
		springBootVersion = '2.1.0.RELEASE'
	}
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
		classpath("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
	}
}

apply plugin: 'kotlin'
apply plugin: 'kotlin-spring'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group = 'io.pivotal.syanagihara'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = 1.8
compileKotlin {
	kotlinOptions {
		freeCompilerArgs = ["-Xjsr305=strict"]
		jvmTarget = "1.8"
	}
}
compileTestKotlin {
	kotlinOptions {
		freeCompilerArgs = ["-Xjsr305=strict"]
		jvmTarget = "1.8"
	}
}

repositories {
	mavenCentral()
}


dependencies {
	implementation('org.springframework.boot:spring-boot-starter-web')
	implementation('com.fasterxml.jackson.module:jackson-module-kotlin')
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation('org.springframework.boot:spring-boot-starter-test')
}
```

### Mainクラス の比較

Mainクラスの比較も、Java / Kotlin で全く同じですね。
Main メソッドの書式が Java スタイルか、Kotlin Kotlin スタイルかの違いだけです。

```java
public static void main(String[] args) {
	SpringApplication.run(SimpleApplication.class, args);
}
```

```kotlin
fun main(args: Array<String>) {
    runApplication<SimpleApplication>(*args)
}
```

#### 配列

Kotlin では、Java における配列が存在しません。そこで、Array オブジェクトを用いて配列表現を行います。

Java の配列と Kotlin の配列で異なるのは、定義の仕方というよりも、性質が異なります。

- Java 配列: 共変
    - 型パラメータ間の継承関係を考慮しない
- Kotlin 配列: 不変
    - 型パラメータ間の継承関係を考慮

例えば、Mainメソッドのパラメータで利用しているArray<String>を、Array<Any>に対して代入するような事がKotlinではできません。
型を安全に保てるので、実行時エラーのリスクを回避することができます。

#### 配列の展開

Java ではみなれない次のようなパラメータの記述があります。

```kotlin
*args
``` 

これは、配列の要素を展開して使用するときに利用する書式です。

#### Java

```java
package io.pivotal.syanagihara.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimpleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleApplication.class, args);
	}
}
```

#### Kotlin

```kotlin
package io.pivotal.syanagihara.simple

import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SimpleApplication

fun main(args: Array<String>) {
    runApplication<SimpleApplication>(*args)
}
```