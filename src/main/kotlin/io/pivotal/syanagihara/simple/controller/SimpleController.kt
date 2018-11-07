package io.pivotal.syanagihara.simple.controller

import io.pivotal.syanagihara.simple.data.Message
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/simple")
class SimpleController {

    @GetMapping(value = ["/display"])
    fun getMessages() : List<Message> {
        return listOf(
                Message(
                        UUID.randomUUID().toString(),
                        "First Message",
                        "This is a 1st message on ${getDate()}."
                ),
                Message(UUID.randomUUID().toString(),
                        "Second Message",
                        "This is a 2nd message on ${getDate()}."
                )
        )
    }

    private fun getDate() : String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return simpleDateFormat.format(Date())
    }

    @PutMapping(value = ["/insert"])
    fun insertMessage(@RequestBody message: Message) : Message {
        message.id = UUID.randomUUID().toString()
        return message
    }

    @PostMapping(value = ["/update"])
    fun updateMessage(@RequestBody message: Message) : Message {
        message.title += "UPDATED TITLE:${getDate()}"
        message.message += "UPDATED MESSAGE:${getDate()}"
        return message
    }
}