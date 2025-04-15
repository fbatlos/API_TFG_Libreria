package com.es.aplicacion.repository

import com.es.aplicacion.model.Libro
import com.es.aplicacion.repository.impl.LibroCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface LibroRepository: MongoRepository<Libro, String>,LibroCustomRepository {

    fun findByIsbn13(ISBN: String): Optional<Libro>

    @Query("{ \$or: [{ 'categoria': { \$regex: ?0, \$options: 'i' } },{ 'autor': { \$regex: ?1, \$options: 'i' } }] }")
    fun buscarPorCategoriaOAutor(categoria: String, autor: String): Optional<List<Libro>>

    @Query("{ '\$or': [ { 'titulo': { \$regex: ?0, \$options: 'i' } }, { 'autores': { \$regex: ?0, \$options: 'i' } } ] }")
    fun findByTituloOAutorContainingIgnoreCase(titulo: String): List<Libro>
}