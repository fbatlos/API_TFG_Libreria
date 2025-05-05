package com.es.aplicacion.service

import com.es.aplicacion.model.Compra
import com.es.aplicacion.model.TipoDePago
import com.stripe.model.PaymentIntent
import com.stripe.model.checkout.Session
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.checkout.SessionCreateParams
import org.springframework.stereotype.Service

@Service
class PaymentService {

    fun crearPago(compra: Compra): PaymentIntent {
        val total = compra.items.sumOf { it.precio }

        val paymentMethodTypes = when (compra.metodoDePago.tipo) {
            //Mirar como sería en cada caso
            TipoDePago.STRIPE_CARD -> listOf("card")
            TipoDePago.GOOGLE_PAY -> listOf("card")
            TipoDePago.APPLE_PAY -> listOf("card")
        }

        val params = PaymentIntentCreateParams.builder()
            .setAmount(total.toLong())
            .setCurrency("eur")
            .addAllPaymentMethodType(paymentMethodTypes)
            .putMetadata("usuarioId", compra.usuarioId)
            .build()

        return PaymentIntent.create(params)
    }

    fun crearCheckoutSession(compra: Compra): Session {
        val total = compra.items.sumOf { it.precio }

        val lineItems = compra.items.map { item ->
            SessionCreateParams.LineItem.builder()
                .setQuantity(1 )
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount(item.precio.toLong())
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(item.libroId)
                                .build()
                        )
                        .build()
                )
                .build()
        }

        val paramsBuilder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("https://success")
            .setCancelUrl("https://cancel")

        lineItems.forEach { paramsBuilder.addLineItem(it) }

        val params = paramsBuilder.build()

        return Session.create(params)
    }

}
