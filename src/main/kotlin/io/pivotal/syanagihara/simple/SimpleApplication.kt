package io.pivotal.syanagihara.simple

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class SimpleApplication {

    fun main(args: Array<String>) {
        runApplication<SimpleApplication>(*args)
    }
}