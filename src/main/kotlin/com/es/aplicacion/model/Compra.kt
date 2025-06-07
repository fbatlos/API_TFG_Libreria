package com.es.aplicacion.model

import com.es.aplicacion.dto.LibroDTO
import org.springframework.data.mongodb.core.mapping.Document
import io.swagger.v3.oas.annotations.media.Schema


@Document("Compras")
@Schema(description = "Modelo que representa una compra realizada por un usuario.")
data class Compra(

    @Schema(description = "Nombre de usuario que realizó la compra", example = "johndoe")
    val usuarioName: String,

    @Schema(description = "Lista de items comprados")
    var items: List<ItemCompra>,

    @Schema(description = "Fecha en la que se realizó la compra", example = "2024-06-07")
    val fechaCompra: String,

    @Schema(description = "Dirección de envío para la compra")
    val direccion: Direccion
)

@Schema(description = "Modelo que representa un item dentro de una compra.")
data class ItemCompra(

    @Schema(description = "Información del libro comprado")
    val libro: LibroDTO,

    @Schema(description = "Cantidad de unidades compradas", example = "2")
    var cantidad: Int
)
