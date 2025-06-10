package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document("Tickets")
data class Ticket(
    @BsonId
    val _id : String? = null,
    val userName: String,
    var titulo:String,
    var cuerpo:String
)