package com.es.aplicacion.controller

import com.es.aplicacion.model.Usuario
import com.es.aplicacion.model.Valoracion
import com.es.aplicacion.service.ValoracionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/valoracion")
@Tag(name = "Valoraciones", description = "Operaciones relacionadas con valoraciones de libros")
class ValoracionController {

    @Autowired
    private lateinit var valoracionService: ValoracionService

    @Operation(
        summary = "Obtener valoraciones de un libro",
        description = "Devuelve todas las valoraciones asociadas a un libro dado por su ID"
    )
    @GetMapping("/{libro_id}")
    fun getValoraciones(
        @PathVariable(value = "libro_id") libroId: String
    ): ResponseEntity<List<Valoracion>?> {
        return ResponseEntity(valoracionService.gatValoraciones(libroId), HttpStatus.OK)
    }

    @Operation(
        summary = "Añadir una valoración",
        description = "Permite a un usuario autenticado añadir una nueva valoración a un libro"
    )
    @PostMapping("/add")
    fun addValoracion(
        @RequestBody valoracion: Valoracion,
        authentication: Authentication
    ): ResponseEntity<String> {
        return ResponseEntity(valoracionService.addValoracion(valoracion),HttpStatus.CREATED)
    }

    @Operation(
        summary = "Eliminar una valoración",
        description = "Elimina una valoración existente por su ID (solo si pertenece al usuario autenticado)"
    )
    @DeleteMapping("/eliminar/{idValoracion}")
    fun removeValoracion(
        @PathVariable(value = "idValoracion") idValoracion: String,
        authentication: Authentication
    ): ResponseEntity<String> {
        valoracionService.deleteValoracion(idValoracion,authentication)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Obtener mis valoraciones",
        description = "Devuelve todas las valoraciones realizadas por el usuario autenticado"
    )
    @GetMapping("/mis-valoraciones")
    fun getMisValoraciones(
        authentication: Authentication
    ): ResponseEntity<List<Valoracion>>{
        val valoraciones = valoracionService.getMisValoraciones(authentication)
        return ResponseEntity(valoraciones,HttpStatus.OK)
    }

    @Operation(
        summary = "Generar valoraciones de prueba",
        description = "Genera valoraciones aleatorias de prueba en la base de datos (solo para testing y para que existan datoss con los que trabajar)"
    )
    @PostMapping("/poblar")
    fun poblarValoraciones(): ResponseEntity<String> {
        //valoracionService.poblarValoracionesAleatorias()
        return ResponseEntity("Valoraciones aleatorias generadas", HttpStatus.CREATED)
    }
}