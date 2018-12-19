package io.pivotal.syanagihara.simple.controller

import io.pivotal.syanagihara.simple.service.AsyncTaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/async")
class AsyncTaskController {

    @Autowired
    private lateinit var service: AsyncTaskService

    @GetMapping("/high")
    fun callPrioritizedTask() = service.prioritizedTask()

    @GetMapping("/normal")
    fun callNormalTask() = service.normalTask()

    @GetMapping
    fun callStandardTask() = service.standardTask()


}
