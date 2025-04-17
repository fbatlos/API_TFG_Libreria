package com.es.aplicacion.controller

import com.es.aplicacion.model.Libro
import com.es.aplicacion.model.Valoracion
import com.es.aplicacion.service.LibroService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/libros")
class LibroController {

    @Autowired
    private lateinit var libroService: LibroService


    @PostMapping("/admin/libro")
    fun addLibro(
        @RequestBody libro: Libro ,
        authentication: Authentication
    ):ResponseEntity<Boolean> {
        return ResponseEntity(libroService.addLibro(libro), HttpStatus.CREATED)
    }

    @PutMapping("/admin/libros/{id}")
    fun putLibro(
        @PathVariable id:String,
        @RequestBody libro: Libro,
        authentication: Authentication
    ):ResponseEntity<Boolean> {
        return ResponseEntity(libroService.putLibro(id,libro),HttpStatus.OK)
    }

    @DeleteMapping("/admin/libros/{id}")
    fun deleteLibro(
        @PathVariable id:String,
        authentication: Authentication
    ):ResponseEntity<Boolean> {
        libroService.deleteLibro(id)
        return ResponseEntity.noContent().build()
    }



    @GetMapping
    fun listarLibros(
        @RequestParam(required = false) categoria: String?,
        @RequestParam(required = false) autor: String?
    ): ResponseEntity<List<Libro>>{
        return ResponseEntity(libroService.getLibros(categoria, autor), HttpStatus.OK)
    }
    @GetMapping("/buscar")
    fun buscarLibros(
        @RequestParam(required = false) query: String?,
        authentication: Authentication
    ): ResponseEntity<List<Libro>> {
        val libros = libroService.buscarLibros(query)
        return ResponseEntity.ok(libros)
    }


}