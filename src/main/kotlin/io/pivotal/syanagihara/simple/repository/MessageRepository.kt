package io.pivotal.syanagihara.simple.repository

import io.pivotal.syanagihara.simple.data.Message
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MessageRepository : CrudRepository<Message, String> {

    @Query("from Message m where m.id = ?1")
    fun findMessageIdIs(id: String): Iterable<Message>

}