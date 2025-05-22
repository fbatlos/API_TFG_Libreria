package com.es.aplicacion.service

import com.es.aplicacion.dto.LibroDTO
import com.es.aplicacion.model.Compra
import com.es.aplicacion.model.ItemCompra
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import org.springframework.stereotype.Service

@Service
class PaymentService {

    fun crearCheckoutSession(compra: Compra): Session {
        val total = compra.items.sumOf { it.libro.precio?.toDouble()?.times(it.cantidad) ?: 0.0 }
        compra.items = compra.items.toMutableList()

        if (total <= 50){
            (compra.items as MutableList<ItemCompra>).add(ItemCompra(LibroDTO(null,"Envio",5.99,"eur"),1))
        }

        val lineItems = compra.items.map { (libro, cantidad) -> buildSession(libro, cantidad) }

        val paramsBuilder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("https://gainful-melon-timpani.glitch.me/paginaExito.html")
            .setCancelUrl("https://gainful-melon-timpani.glitch.me/paginaFallo.html")


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
