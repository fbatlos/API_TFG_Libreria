package com.es.aplicacion.repository

import com.es.aplicacion.model.Ticket
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : MongoRepository<Ticket, String> {
}