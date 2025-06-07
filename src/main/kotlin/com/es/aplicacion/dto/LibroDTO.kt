package com.es.aplicacion.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object para representar información básica de un libro")
data class LibroDTO(
    @Schema(description = "Identificador único del libro", example = "64b2e1f7a1234c56789d0ef1", nullable = true)
    val _id: String?,

    @Schema(description = "Título del libro", example = "El Quijote")
    val titulo: String,

    @Schema(description = "Precio del libro", example = "19.99")
    val precio: Double,

    @Schema(description = "Moneda en la que se expresa el precio", example = "EUR")
    val moneda: String
)
