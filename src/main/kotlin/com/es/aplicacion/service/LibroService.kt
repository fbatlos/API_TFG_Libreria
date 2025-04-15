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
class LibroService {
    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var valoracionRepository: ValoracionRepository


    fun addLibro(libro: Libro): Boolean {
        if(libroRepository.findByIsbn13(libro.isbn13!!).isPresent){Conflict("El libro ya existe.")}

        Utils.validacionesLibroSimples(libro)

        libroRepository.save(libro)
        return true
    }

    fun putLibro(id:String,libro: Libro): Boolean {
        val libroExistente = libroRepository.findById(id).orElseThrow{NotFound("Libro no encontrado.")}

        Utils.validacionesLibroSimples(libro)

        libroExistente.imagen = libro.imagen
        libroExistente.precio = libro.precio
        libroExistente.titulo = libro.titulo
        libroExistente.autores = libro.autores
        libroExistente.categorias = libro.categorias
        libroExistente.moneda = libro.moneda


        libroRepository.save(libroExistente)

        return true
    }

    fun deleteLibro(id: String): Boolean {
        libroRepository.findById(id).orElseThrow { BadRequest("El libro no encontrado.") }

        libroRepository.deleteById(id)
        return true
    }



    fun getLibros(categoria:String?,autor:String?): List<Libro> {
        return when {
            categoria != null || autor != null -> libroRepository.buscarPorCategoriaOAutor(categoria?:"",autor?:"").orElseThrow { NotFound("No se ha encontrado.") }
            else -> libroRepository.findAll()
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