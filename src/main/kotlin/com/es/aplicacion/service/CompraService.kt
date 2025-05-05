package com.es.aplicacion.service

import com.es.aplicacion.model.Compra
import com.es.aplicacion.model.Libro
import com.stripe.model.PaymentIntent
import com.stripe.model.checkout.Session
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.checkout.SessionCreateParams
import org.springframework.stereotype.Service

@Service
class PaymentService {

    fun crearPago(compra: Compra): PaymentIntent {
        val total = 0.0


        val params = PaymentIntentCreateParams.builder()
            .setAmount(total.toLong())
            .setCurrency("eur")
            .putMetadata("usuario", compra.usuarioName)
            .build()

        return PaymentIntent.create(params)
    }

    fun crearCheckoutSession(compra: Compra): Session {

        val lineItems = buildLineItems(compra)

        val paramsBuilder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("https://success")
            .setCancelUrl("https://cancel")

        lineItems.forEach { paramsBuilder.addLineItem(it) }

        val params = paramsBuilder.build()

        return Session.create(params)
    }

}

fun buildLineItems(compra: Compra): List<SessionCreateParams.LineItem> {
    return compra.items.flatMap { item ->
        item.map { (libro, cantidad) ->
            SessionCreateParams.LineItem.builder()
                .setQuantity(cantidad.toLong()) // Cantidad de veces que han comprado ese libro
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount((libro.precio?.toLong() ?: 0L)) // Precio unitario
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(libro.titulo)
                                .build()
                        )
                        .build()
                )
                .build()
        }
    }
}