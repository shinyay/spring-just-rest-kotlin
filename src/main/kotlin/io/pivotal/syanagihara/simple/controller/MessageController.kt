package io.pivotal.syanagihara.simple.controller

import io.pivotal.syanagihara.simple.data.Message
import io.pivotal.syanagihara.simple.service.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/messages")
class MessageController() {

    @Autowired
    private lateinit var service: MessageService

    @GetMapping
    fun getMessages() = service.getMessages()

    private fun getDate() : String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return simpleDateFormat.format(Date())
    }

    @PostMapping
    fun insertMessage(@RequestBody message: Message) : Message = service.insertMessage(message)

    @PutMapping
    fun updateMessage(@RequestBody message: Message) : Boolean = service.updateMessage(message)

    @DeleteMapping(value = ["/{id}"])
    fun deleteMessage(@PathVariable(name = "id") id: String): Boolean = service.deleteMessage(id)
}