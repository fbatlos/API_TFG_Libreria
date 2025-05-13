package com.es.aplicacion.service

import com.es.aplicacion.dto.LibroDTO
import com.es.aplicacion.model.Compra
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

        val lineItems = compra.items.map { (libro, cantidad) -> buildSession(libro, cantidad) }

        val paramsBuilder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("https://leafread.app/pago_exitoso")
            .setCancelUrl("https://leafread.app/pago_cancelado")


        lineItems.forEach { paramsBuilder.addLineItem(it) }

        val params = paramsBuilder.build()

        return Session.create(params)
    }

    fun obtenerEstadoPago(sesion:String):String {
        val resource =
            Session.retrieve(
                sesion
            )
        println(resource)
        return resource.paymentStatus
    }

}

fun buildSession(libro: LibroDTO, cantidad: Int): SessionCreateParams.LineItem {
    println(libro.precio.toLong())
    return SessionCreateParams.LineItem.builder()
        .setQuantity(cantidad.toLong())
        .setPriceData(
            SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("eur")
                .setUnitAmount((libro.precio?.times(100)?.toLong() ?: 0L))
                .setProductData(
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(libro.titulo)
                        .build()
                )
                .build()
        )
        .build()
}
