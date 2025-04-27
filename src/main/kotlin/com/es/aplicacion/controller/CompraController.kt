package com.es.aplicacion.controller

import com.es.aplicacion.model.Compra
import com.es.aplicacion.service.PaymentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/compra")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/crear")
    fun crearPago(@RequestBody compra: Compra): Map<String, String> {
        val paymentIntent = paymentService.crearPago(compra)
        return mapOf("clientSecret" to paymentIntent.clientSecret)
    }
}
