package com.es.aplicacion.service

import com.es.aplicacion.model.Compra
import com.es.aplicacion.model.TipoDePago
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
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
}
