package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.Binary
import org.springframework.data.mongodb.core.mapping.Document


@Document("Avatares")
data class Avatar(
    @BsonId
    val _id: String? = null,
    val filename: String,
    val mimeType: String,
    val data: Binary // Base64 string
)