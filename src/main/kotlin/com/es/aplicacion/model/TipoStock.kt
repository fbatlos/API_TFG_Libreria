package com.es.aplicacion.model

import io.swagger.v3.oas.annotations.media.Schema
enum class TipoStock {
    @Schema(description = "Stock disponible")
    DISPONIBLE,

    @Schema(description = "Stock agotado")
    AGOTADO,

    @Schema(description = "Stock preventa")
    PREVENTA
}