package com.es.aplicacion.controller

import com.es.aplicacion.model.Ticket
import com.es.aplicacion.service.TicketService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ticket")
@Tag(name = "ticket", description = "Operaciones relacionadas con las dudas de los usuarios")
class TicketsController {
    @Autowired
    lateinit var ticketService: TicketService

    @Operation(
        summary = "Obtiene todos los tickets si es admin",
    )
    @GetMapping("/admin/tickets")
    fun getAllTickets(authentication: Authentication): ResponseEntity<List<Ticket>> {
        return ResponseEntity.ok(ticketService.getAllTickets())
    }

    @Operation(
        summary = "Elimina un ticket",
    )
    @DeleteMapping("/admin/ticket/{idTicket}")
    fun deleteTicket(@PathVariable(value = "idTicket") ticketId: String, authentication: Authentication): ResponseEntity<String> {
        ticketService.deleteTicket(ticketId, authentication)
        return  ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Envia un ticket"
    )
    @PostMapping("/enviar")
    fun enviar(@RequestBody ticket: Ticket,authentication: Authentication): ResponseEntity<Ticket> {
        return ResponseEntity.ok(ticketService.enviarTicket(ticket,authentication))
    }
}