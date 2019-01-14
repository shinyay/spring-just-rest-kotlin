package io.pivotal.syanagihara.simple.data

import com.fasterxml.jackson.annotation.JsonInclude
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "message")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Message(
        @Id
        @GeneratedValue(generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "uuid2")
        @Column(columnDefinition = "varchar(36)")
        var id: String = "",
        var title: String,
        var message: String,
        @CreationTimestamp
        var created: Date = Date(),
        @UpdateTimestamp
        var updated: Date = Date()
){
        constructor() : this("","","")
}