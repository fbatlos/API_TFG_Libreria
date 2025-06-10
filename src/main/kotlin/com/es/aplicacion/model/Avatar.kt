package com.es.aplicacion.model

import io.swagger.v3.oas.annotations.media.Schema
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.Binary
import org.springframework.data.mongodb.core.mapping.Document


@Document("Avatares")
@Schema(description = "Modelo que representa un avatar de usuario.")
data class Avatar(

    @BsonId
    @Schema(description = "Identificador único del avatar", example = "663c8d2f7c08c03c55d6b26e")
    val _id: String? = null,

    @Schema(description = "Nombre del archivo de imagen", example = "avatar1.png")
    val filename: String,

    @Schema(description = "Tipo MIME del archivo de imagen", example = "image/png")
    val mimeType: String,

    @Schema(description = "Contenido binario de la imagen codificado en base64")
    val data: ByteArray
)