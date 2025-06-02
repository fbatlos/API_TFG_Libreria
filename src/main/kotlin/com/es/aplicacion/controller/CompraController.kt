package com.es.aplicacion.controller

import com.es.aplicacion.model.Compra
import com.es.aplicacion.service.CompraService
import com.es.aplicacion.service.LibroService
import com.es.aplicacion.service.PaymentService
import com.es.aplicacion.service.UsuarioService
import com.stripe.model.checkout.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/compra")
class CompraController{

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var compraService: CompraService


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
        val estado = paymentService.obtenerEstadoPago(sessionId)
        return mapOf("estatus" to estado)
    }

    @PostMapping("/ticket")
    fun addTicketCompra(@RequestBody compra: Compra, authentication: Authentication): Map<String, Boolean> {
        val ticket = compraService.addTicketCompra(compra)
        return mapOf("creado" to ticket)
    }

    @GetMapping("/tickets")
    fun obtenerTickets(
        authentication: Authentication
    ): MutableList<Compra> {
        val compras = compraService.obtenerCompras(authentication.name)
        return compras
    }

    @GetMapping("/admin/tickets")
    fun obtenerAllCompras(): MutableList<Compra> {
        val compras = compraService.obtenerAllCompras()
        return compras
    }

    @PostMapping("/actualizar-stock")
    fun realizarCompra(
        @RequestBody compra: Compra,
        authentication: Authentication
    ): ResponseEntity<String> {

        compraService.actualizarStock(compra, authentication)
        return ResponseEntity.ok("Compra realizada correctamente.")
    }

}