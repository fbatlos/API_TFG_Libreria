package com.es.aplicacion.repository

import com.es.aplicacion.model.Compra
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CompraRepository : MongoRepository<Compra, String> {
    fun findByUsuarioName(usuarioName: String): List<Compra>
}