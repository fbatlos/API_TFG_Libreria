package com.es.aplicacion.model

import com.es.aplicacion.dto.LibroDTO
import org.springframework.data.mongodb.core.mapping.Document

@Document("Compras")
data class Compra(
    val usuarioName: String,
    val items: List<ItemCompra>
)


data class ItemCompra(
    val libro: LibroDTO,
    var cantidad: Int
)
