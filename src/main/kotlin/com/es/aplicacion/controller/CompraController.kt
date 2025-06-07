package com.es.aplicacion.controller

import com.es.aplicacion.model.Compra
import com.es.aplicacion.service.CompraService
import com.es.aplicacion.service.LibroService
import com.es.aplicacion.service.PaymentService
import com.es.aplicacion.service.UsuarioService
import com.stripe.model.checkout.Session
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/compra")
@Tag(name = "Compras", description = "Operaciones relacionadas con las compras y pagos de libros")
class CompraController{

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var compraService: CompraService

    @Operation(
        summary = "Crear sesión de checkout",
        description = "Inicia una sesión de pago en Stripe con los datos de la compra"
    )
    @PostMapping("/checkout")
    fun crearCheckoutSession(@RequestBody compra: Compra, authentication: Authentication): Map<String, String> {
        val session = paymentService.crearCheckoutSession(compra)
        return mapOf(
            "sessionId" to session.id,
            "url" to session.url
        )
    }

    @Operation(
        summary = "Obtener estado de pago",
        description = "Devuelve el estado actual de una sesión de pago en Stripe usando su ID"
    )
    @GetMapping("/estado/{sessionId}")
    fun obtenerEstadoPago(@PathVariable sessionId: String): Map<String, String> {
        val estado = paymentService.obtenerEstadoPago(sessionId)
        return mapOf("estatus" to estado)
    }

    @Operation(
        summary = "Registrar ticket de compra",
        description = "Guarda un ticket de compra en base de datos tras completarse el pago"
    )
    @PostMapping("/ticket")
    fun addTicketCompra(@RequestBody compra: Compra, authentication: Authentication): Map<String, Boolean> {
        val ticket = compraService.addTicketCompra(compra)
        return mapOf("creado" to ticket)
    }

    @Operation(
        summary = "Obtener tickets de compra del usuario",
        description = "Devuelve todos los tickets de compra asociados al usuario autenticado"
    )
    @GetMapping("/tickets")
    fun obtenerTickets(
        authentication: Authentication
    ): MutableList<Compra> {
        val compras = compraService.obtenerCompras(authentication.name)
        return compras
    }

    @Operation(
        summary = "Obtener todas las compras (admin)",
        description = "Devuelve todas las compras registradas en el sistema. Solo para administradores"
    )
    @GetMapping("/admin/tickets")
    fun obtenerAllCompras(): MutableList<Compra> {
        val compras = compraService.obtenerAllCompras()
        return compras
    }

    @Operation(
        summary = "Realizar compra y actualizar stock",
        description = "Actualiza el stock de los libros tras realizar una compra"
    )
    @PostMapping("/actualizar-stock")
    fun realizarCompra(
        @RequestBody compra: Compra,
        authentication: Authentication
    ): ResponseEntity<String> {

        compraService.actualizarStock(compra, authentication)
        return ResponseEntity.ok("Compra realizada correctamente.")
    }

}