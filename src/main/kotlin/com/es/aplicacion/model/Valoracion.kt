﻿package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.Date

@Document("Valoraciones")
data class Valoracion(
    @BsonId
    val _id : String?,
    val libroid : String,
    val usuarioName : String,
    val valoracion : Int,
    val comentario:String,
    val fecha : LocalDateTime = LocalDateTime.now()
)