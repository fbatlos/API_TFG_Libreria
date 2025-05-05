package com.es.aplicacion.controller

import com.es.aplicacion.model.Compra
import com.es.aplicacion.service.PaymentService
import com.stripe.model.checkout.Session
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/compra")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/crear")
    fun crearPago(@RequestBody compra: Compra,authentication: Authentication): Map<String, String> {
        val paymentIntent = paymentService.crearPago(compra)
        return mapOf("clientSecret" to paymentIntent.clientSecret)
    }

    @PostMapping("/checkout")
    fun crearCheckoutSession(@RequestBody compra: Compra, authentication: Authentication): Map<String, String> {
        val session = paymentService.crearCheckoutSession(compra)
        return mapOf(
            "sessionId" to session.id,
            "url" to session.url
        )
    }

    @GetMapping("/estado/{sessionId}")
    fun obtenerEstadoPago(@PathVariable sessionId: String): Map<String, String> {
        val session = Session.retrieve(sessionId)
        return mapOf("status" to session.paymentStatus)
    }


}
