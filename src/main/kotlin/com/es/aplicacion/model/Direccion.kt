package com.es.aplicacion.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Modelo que representa una dirección postal.")
data class Direccion(

    @Schema(description = "Calle de la dirección", example = "Calle Mayor")
    val calle: String,

    @Schema(description = "Número de la calle", example = "123")
    val num: String,

    @Schema(description = "Municipio o ciudad", example = "Madrid")
    val municipio: String,

    @Schema(description = "Provincia o región", example = "Madrid")
    val provincia: String
)

