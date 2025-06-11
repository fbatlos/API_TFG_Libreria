package com.es.aplicacion.model


import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import io.swagger.v3.oas.annotations.media.Schema


@Document("Libros")
@Schema(description = "Modelo que representa un libro.")
data class Libro(

    @BsonId
    @Schema(description = "Identificador único del libro", example = "60d5f483d8d6d12f4c8c4f56")
    val _id: String? = null,

    @Schema(description = "Título del libro", example = "El Quijote")
    var titulo: String? = null,

    @Schema(description = "Lista de autores del libro", example = "[\"Miguel de Cervantes\"]")
    var autores: List<String> = emptyList(),

    @Schema(description = "Descripción del libro", example = "Una novela clásica española...")
    var descripcion: String? = null,

    @Schema(description = "Precio del libro", example = "19.99")
    var precio: Double? = null,

    @Schema(description = "Moneda en la que está el precio", example = "EUR")
    var moneda: String? = "EUR",

    @Schema(description = "URL o path de la imagen de portada", example = "https://ejemplo.com/portada.jpg")
    var imagen: String? = null,

    @Schema(description = "Enlace al ebook", example = "https://ejemplo.com/ebook.pdf")
    var enlaceEbook: String? = null,

    @Schema(description = "ISBN-13 del libro", example = "9781234567897")
    var isbn13: String? = null,

    @Schema(description = "Categorías o géneros del libro", example = "[\"Novela\", \"Clásico\"]")
    var categorias: List<String> = emptyList(),

    @Schema(description = "Valoración media del libro", example = "4.5")
    var valoracionMedia: Double? = 0.0,

    @Schema(description = "Información del stock del libro")
    var stock: Stock = Stock(TipoStock.AGOTADO, 0)
)
