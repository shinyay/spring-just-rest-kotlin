package io.pivotal.syanagihara.simple.data

import java.util.*

data class MessageDTO(
        var title: String,
        var message: String
) {
    var id: String = ""
    var created: Date = Date()
    var updated: Date = Date()

    constructor(message: Message) : this(
            message.title,
            message.message
    ){
        id = message.id
        created = message.created
        updated = message.updated
    }
}