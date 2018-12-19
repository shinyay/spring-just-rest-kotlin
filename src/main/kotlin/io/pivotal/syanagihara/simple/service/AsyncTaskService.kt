package io.pivotal.syanagihara.simple.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service("Async Task Service")
class AsyncTaskService {

    val logger = LoggerFactory.getLogger(this::class.java.name)

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

    @Async
    fun standardTask() {
        logger.info("Normal Task Start")
        TimeUnit.SECONDS.sleep(5)
        logger.info("Normal Task End")
    }
}
