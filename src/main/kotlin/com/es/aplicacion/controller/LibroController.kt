package com.es.aplicacion.controller

import com.es.aplicacion.model.Libro
import com.es.aplicacion.model.Valoracion
import com.es.aplicacion.service.LibroService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/libros")
@Tag(name = "Libros", description = "Operaciones relacionadas con los libros disponibles en la tienda")
class LibroController {

    @Autowired
    private lateinit var libroService: LibroService

    @Operation(
        summary = "Añadir un libro (admin)",
        description = "Permite a un administrador añadir un nuevo libro a la tienda"
    )
    @PostMapping("/admin/libro")
    fun addLibro(
        @RequestBody libro: Libro ,
        authentication: Authentication
    ):ResponseEntity<Boolean> {
        return ResponseEntity(libroService.addLibro(libro), HttpStatus.CREATED)
    }

    @Operation(
        summary = "Actualizar un libro (admin)",
        description = "Permite a un administrador actualizar los datos de un libro existente por su ID"
    )
    @PutMapping("/admin/libros/{id}")
    fun putLibro(
        @PathVariable id:String,
        @RequestBody libro: Libro,
        authentication: Authentication
    ):ResponseEntity<Boolean> {
        return ResponseEntity(libroService.putLibro(id,libro),HttpStatus.OK)
    }

    @Operation(
        summary = "Eliminar un libro (admin)",
        description = "Permite a un administrador eliminar un libro existente por su ID"
    )
    @DeleteMapping("/admin/libros/{id}")
    fun deleteLibro(
        @PathVariable id:String,
        authentication: Authentication
    ):ResponseEntity<Boolean> {
        libroService.deleteLibro(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Listar libros",
        description = "Devuelve todos los libros disponibles, pudiendo filtrar opcionalmente por categoría y/o autor"
    )
    @GetMapping
    fun listarLibros(
        @RequestParam(required = false) categoria: String?,
        @RequestParam(required = false) autor: String?
    ): ResponseEntity<List<Libro>>{
        return ResponseEntity(libroService.getLibros(categoria, autor), HttpStatus.OK)
    }

    @Operation(
        summary = "Buscar libros",
        description = "Permite buscar libros por coincidencia de nombre o atributos relacionados"
    )
    @GetMapping("/buscar")
    fun buscarLibros(
        @RequestParam(required = false) query: String?,
        authentication: Authentication
    ): ResponseEntity<List<Libro>> {
        val libros = libroService.buscarLibros(query)
        return ResponseEntity.ok(libros)
    }
}