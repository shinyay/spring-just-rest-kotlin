package io.pivotal.syanagihara.simple.service

import io.pivotal.syanagihara.simple.data.Message
import io.pivotal.syanagihara.simple.data.MessageDTO
import io.pivotal.syanagihara.simple.repository.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service("Message Service")
class MessageService {

    @Autowired
    lateinit var repository: MessageRepository

    fun getMessages() : Iterable<MessageDTO> = repository.findAll().map { it -> MessageDTO(it) }

    private fun getDate() : String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return simpleDateFormat.format(Date())
    }

    fun insertMessage(messageDto: MessageDTO) = MessageDTO(
            repository.save(Message(
                                    title = messageDto.title,
                                    message = messageDto.message
            ))
    )

    fun updateMessage(messageDto: MessageDTO) : MessageDTO {
        var message = repository.findById(messageDto.id).get()
        message.title =messageDto.title
        message.message = messageDto.message
        message.updated = Date()
        return MessageDTO(repository.save(message))
    }

    fun deleteMessage(id: String) = repository.deleteById(id)
}