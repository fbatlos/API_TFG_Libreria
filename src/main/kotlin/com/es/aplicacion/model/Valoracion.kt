package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("Valoraciones")
data class Valoracion(
    @BsonId
    val _id : String?,
    val libro_id : String,
    val usuario_id : String,
    val valoracion : Int,
    val comentario:String,
    val fecha : Date,
)