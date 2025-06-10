package com.es.aplicacion.repository

import com.es.aplicacion.model.Libro
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface LibroRepository: MongoRepository<Libro, String> {

    fun findByIsbn13(ISBN: String): Optional<Libro>

    @Query("{ 'categorias': { \$regex: ?0, \$options: 'i' } }")
    fun buscarPorCategoriaSimilar(termino: String): List<Libro>

    @Query("{ 'autores': { \$regex: ?0, \$options: 'i' } }")
    fun buscarPorAutorSimilar(termino: String): List<Libro>

    @Query("{\$or: [{ 'categorias': { \$regex: ?0, \$options: 'i' } },{ 'autores': { \$regex: ?1, \$options: 'i' } }]}")
    fun buscarPorCategoriaOAutorSimilar(
        terminoCategoria: String,
        terminoAutor: String
    ): List<Libro>

    @Query("{ '\$or': [ { 'titulo': { \$regex: ?0, \$options: 'i' } }, { 'autores': { \$regex: ?0, \$options: 'i' } } ] }")
    fun findByTituloOAutorContainingIgnoreCase(titulo: String): List<Libro>
}