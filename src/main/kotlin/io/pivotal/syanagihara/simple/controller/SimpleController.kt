package io.pivotal.syanagihara.simple.controller

import io.pivotal.syanagihara.simple.data.Message
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/simple")
class SimpleController {

    @GetMapping
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
}