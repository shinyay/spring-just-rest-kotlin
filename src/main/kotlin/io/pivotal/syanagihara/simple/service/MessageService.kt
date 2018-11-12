package io.pivotal.syanagihara.simple.service

import io.pivotal.syanagihara.simple.data.Message
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service("Message Service")
class MessageService {

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

    fun insertMessage(message: Message) : Message {
        message.id = UUID.randomUUID().toString()
        return message
    }

    fun updateMessage(message: Message) : Boolean {
        message.title += "UPDATED TITLE:${getDate()}"
        message.message += "UPDATED MESSAGE:${getDate()}"
        return true
    }

    fun deleteMessage(id: String): Boolean = true
}