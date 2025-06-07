package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import io.swagger.v3.oas.annotations.media.Schema

@Document("Valoraciones")
@Schema(description = "Entidad que representa una valoración de un libro realizada por un usuario")
data class Valoracion(
    @BsonId
    @Schema(description = "Identificador único de la valoración", example = "64b2e1f7a1234c56789d0ef1", nullable = true)
    val _id: String?,

    @Schema(description = "Identificador del libro valorado", example = "9781234567897")
    val libroid: String,

    @Schema(description = "Nombre de usuario que realiza la valoración", example = "juan123")
    val usuarioName: String,

    @Schema(description = "Puntuación otorgada al libro (por ejemplo de 1 a 5)", example = "4")
    val valoracion: Int,

    @Schema(description = "Comentario o reseña que acompaña la valoración", example = "Muy buen libro, lo recomiendo.")
    val comentario: String,

    @Schema(description = "Fecha y hora en que se realizó la valoración", example = "2025-06-07T12:34:56")
    val fecha: LocalDateTime = LocalDateTime.now()
)
