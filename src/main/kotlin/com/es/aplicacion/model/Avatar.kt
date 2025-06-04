package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document


@Document("Avatares")
data class Avatar (
    @BsonId
    val _id : String?,
    val BitMap : ByteArray?
)