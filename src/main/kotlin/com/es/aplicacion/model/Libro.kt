package com.es.aplicacion.model


import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("Libros")
data class Libro(
    @BsonId
    val _id : String?,
    var titulo: String? = null,
    var autores: List<String> = emptyList(),
    var precio: Double? = null,
    var moneda: String? = null,
    var imagen: String? = null,
    var enlaceEbook: String? = null,
    var isbn13: String? = null,
    var categorias: List<String> = emptyList(),
    var stock: Stock


)