package com.es.aplicacion.service

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Libro
import com.es.aplicacion.repository.LibroRepository
import com.es.aplicacion.repository.ValoracionRepository
import com.es.aplicacion.util.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
@Service
class LibroService(
    private val libroRepository: LibroRepository,
    private val valoracionRepository: ValoracionRepository
) {

    fun addLibro(libro: Libro): Boolean {
        if(libro.isbn13.isNullOrBlank()){
            throw BadRequest("El isbn del libro no puede ser nulo.")
        }
        if (libroRepository.findByIsbn13(libro.isbn13!!).isPresent) {
            throw Conflict("El libro ya existe.")
        }

        Utils.validacionesLibroSimples(libro)

        libroRepository.save(libro)
        return true
    }

    fun putLibro(id: String, libro: Libro): Boolean {
        val libroExistente = libroRepository.findById(id).orElseThrow { NotFound("Libro no encontrado.") }

        Utils.validacionesLibroSimples(libro)

        libroExistente.imagen = libro.imagen
        libroExistente.precio = libro.precio
        libroExistente.titulo = libro.titulo
        libroExistente.autores = libro.autores
        libroExistente.categorias = libro.categorias
        libroExistente.moneda = libro.moneda
        libroExistente.stock = libro.stock

        libroRepository.save(libroExistente)

        return true
    }

    fun deleteLibro(id: String): Boolean {
        libroRepository.findById(id).orElseThrow { BadRequest("El libro no encontrado.") }

        libroRepository.deleteById(id)
        return true
    }

    fun getLibros(categoria: String?, autor: String?): List<Libro> {
        return when {
            categoria != null && autor != null ->
                libroRepository.buscarPorCategoriaOAutorSimilar(categoria, autor)
            categoria != null ->
                libroRepository.buscarPorCategoriaSimilar(categoria)
            autor != null ->
                libroRepository.buscarPorAutorSimilar(autor)
            else ->
                libroRepository.findAll()
        }
    }

    fun buscarLibros(query: String?): List<Libro> {
        return if (!query.isNullOrBlank()) {
            libroRepository.findByTituloOAutorContainingIgnoreCase(query)
        } else {
            emptyList()
        }
    }
}
