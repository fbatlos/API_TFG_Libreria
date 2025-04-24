package com.es.aplicacion.model

import org.springframework.data.mongodb.core.mapping.Document

@Document("Compras")
data class Compra(
    val usuarioId: String,
    val items: List<CompraItem>,
    val metodoDePago: MetodoDePago
)


data class CompraItem(
    val libroId: String,
    val precio: Int,
    val type: TipoStock
)


data class MetodoDePago(
    val token: String,
    val tipo: TipoDePago
)
enum class TipoDePago {
    STRIPE_CARD, GOOGLE_PAY, APPLE_PAY
}