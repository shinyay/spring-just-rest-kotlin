package io.pivotal.syanagihara.simple

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@SpringBootApplication
@EnableAsync
class SimpleApplication {

    @Bean
    fun normalTaskExecutor(): TaskExecutor  = ThreadPoolTaskExecutor().apply {
        corePoolSize = 1
        maxPoolSize = 1
        setQueueCapacity(5)
        setWaitForTasksToCompleteOnShutdown(true)
    }

    @Bean
    fun prioritizedTaskExecutor(): TaskExecutor  = ThreadPoolTaskExecutor().apply {
        corePoolSize = 1
        maxPoolSize = 1
        setQueueCapacity(5)
        setWaitForTasksToCompleteOnShutdown(true)
    }
}

fun main(args: Array<String>) {
    runApplication<SimpleApplication>(*args)
}