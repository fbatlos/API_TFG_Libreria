package com.es.aplicacion.model

import com.es.aplicacion.dto.LibroDTO
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.Date

@Document("Compras")
data class Compra(
    val usuarioName: String,
    val items: List<ItemCompra>,
    val fechaCompra:LocalDateTime = LocalDateTime.now()
)


data class ItemCompra(
    val libro: LibroDTO,
    var cantidad: Int
)
