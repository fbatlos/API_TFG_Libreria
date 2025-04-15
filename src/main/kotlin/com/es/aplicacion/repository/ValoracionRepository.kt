package com.es.aplicacion.repository

import com.es.aplicacion.model.Libro
import com.es.aplicacion.model.Valoracion
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ValoracionRepository: MongoRepository<Valoracion, String> {
}