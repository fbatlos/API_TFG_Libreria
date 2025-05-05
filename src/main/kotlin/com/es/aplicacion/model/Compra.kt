package com.es.aplicacion.model

import org.springframework.data.mongodb.core.mapping.Document

@Document("Compras")
data class Compra(
    val usuarioName: String,
    val items: Map<Libro, Int>
)