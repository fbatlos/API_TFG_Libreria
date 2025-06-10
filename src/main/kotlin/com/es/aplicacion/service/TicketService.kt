package com.es.aplicacion.service

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.model.Ticket
import com.es.aplicacion.repository.TicketRepository
import com.es.aplicacion.repository.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val usuarioRepository: UsuarioRepository,
) {
    fun getAllTickets(): List<Ticket> {
        return ticketRepository.findAll()
    }

    fun enviarTicket(ticket: Ticket,authentication: Authentication): Ticket {
        usuarioRepository.findByUsername(authentication.name).orElseThrow { BadRequest("El usuario no existe.") }
        if (ticket.titulo.isEmpty() || ticket.cuerpo.isEmpty()){
            throw BadRequest("Todos los campos son obligatorios")
        }
        return ticketRepository.save(ticket)
    }

    fun deleteTicket(idTicket: String,authentication: Authentication){
        val ticket = ticketRepository.findById(idTicket).orElseThrow { BadRequest("El ticket no existe.") }
        ticketRepository.delete(ticket)
    }
}