package io.pivotal.syanagihara.simple.controller

import io.pivotal.syanagihara.simple.data.Message
import io.pivotal.syanagihara.simple.data.MessageDTO
import io.pivotal.syanagihara.simple.service.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/messages")
class MessageController() {

    @Autowired
    private lateinit var service: MessageService

    @GetMapping(
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    fun getMessages() = service.getMessages()

    @PostMapping(
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE),
            consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    fun insertMessage(@RequestBody message: MessageDTO) : MessageDTO = service.insertMessage(message)

    @PutMapping(
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE),
            consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    fun updateMessage(@RequestBody message: MessageDTO) : MessageDTO = service.updateMessage(message)

    @DeleteMapping(
            value = ["/{id}"],
            produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    fun deleteMessage(@PathVariable(name = "id") id: String) = service.deleteMessage(id)
    
}