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

    // Búsqueda flexible para categorías (ej: "juve" encontrará "Juvenil")
    @Query("{ 'categorias': { \$regex: ?0, \$options: 'i' } }")
    fun buscarPorCategoriaSimilar(termino: String): List<Libro>

    // Búsqueda flexible para autores (ej: "row" encontrará "J.K. Rowling")
    @Query("{ 'autores': { \$regex: ?0, \$options: 'i' } }")
    fun buscarPorAutorSimilar(termino: String): List<Libro>

    // Combinación OR con ambos términos
    @Query("{\$or: [{ 'categorias': { \$regex: ?0, \$options: 'i' } },{ 'autores': { \$regex: ?1, \$options: 'i' } }]}")
    fun buscarPorCategoriaOAutorSimilar(
        terminoCategoria: String,
        terminoAutor: String
    ): List<Libro>

    @Query("{ '\$or': [ { 'titulo': { \$regex: ?0, \$options: 'i' } }, { 'autores': { \$regex: ?0, \$options: 'i' } } ] }")
    fun findByTituloOAutorContainingIgnoreCase(titulo: String): List<Libro>
}