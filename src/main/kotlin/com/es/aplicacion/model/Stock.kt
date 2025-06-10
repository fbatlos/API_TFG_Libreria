package com.es.aplicacion.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Información del stock de un libro")
data class Stock(
    @Schema(description = "Tipo de stock del libro", example = "DISPONIBLE")
    var tipo: TipoStock,

    @Schema(description = "Cantidad disponible en stock", example = "10")
    var numero: Int
)