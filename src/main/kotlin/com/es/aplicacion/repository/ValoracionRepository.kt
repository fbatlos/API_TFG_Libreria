package com.es.aplicacion.repository

import com.es.aplicacion.model.Libro
import com.es.aplicacion.model.Valoracion
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.collections.List

@Repository
interface ValoracionRepository: MongoRepository<Valoracion, String> {
    @Query("{ 'libro_id': ?0 }")
    fun findValoracionesByLibroId(libroid: String): Optional<List<Valoracion>>

    @Query("{ 'usuarioid': ?0 }")
    fun findValoracionByUsuarioId(usuarioid: String): Optional<List<Valoracion>>

}
