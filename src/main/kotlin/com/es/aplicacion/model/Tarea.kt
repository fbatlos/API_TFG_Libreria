package com.es.aplicacion.model

import org.bson.types.ObjectId
import java.util.*

data class Tarea(
    var titulo: String,
    var cuerpo : String,
    var username: String,
    val fecha_pub : Date,
    var completada:Boolean = false
)
