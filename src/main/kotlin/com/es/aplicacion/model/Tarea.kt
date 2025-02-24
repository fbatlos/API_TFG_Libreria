package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class Tarea(
    @BsonId
    val _id : String?,
    var titulo: String,
    var cuerpo : String,
    var username: String,
    var fecha_pub : Date,
    var completada:Boolean = false
)
