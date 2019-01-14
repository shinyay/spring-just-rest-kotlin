package io.pivotal.syanagihara.simple.repository

import io.pivotal.syanagihara.simple.data.Message
import org.springframework.data.repository.CrudRepository

interface MessageRepository : CrudRepository<Message, String>