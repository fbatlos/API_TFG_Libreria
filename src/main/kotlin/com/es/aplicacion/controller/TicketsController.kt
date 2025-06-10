package com.es.aplicacion.controller

import com.es.aplicacion.model.Ticket
import com.es.aplicacion.service.TicketService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ticket")
class TicketsController {
    @Autowired
    lateinit var ticketService: TicketService

    @GetMapping("/admin/tickets")
    fun getAllTickets(authentication: Authentication): ResponseEntity<List<Ticket>> {
        return ResponseEntity.ok(ticketService.getAllTickets())
    }

    @DeleteMapping("/admin/ticket/{idTicket}")
    fun deleteTicket(@PathVariable(value = "idTicket") ticketId: String, authentication: Authentication): ResponseEntity<String> {
        ticketService.deleteTicket(ticketId, authentication)
        return  ResponseEntity.noContent().build()
    }

    @PostMapping("/enviar")
    fun enviar(@RequestBody ticket: Ticket,authentication: Authentication): ResponseEntity<Ticket> {
        return ResponseEntity.ok(ticketService.enviarTicket(ticket,authentication))
    }
}