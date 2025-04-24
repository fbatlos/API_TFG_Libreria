package com.es.aplicacion.controller

import com.es.aplicacion.model.Usuario
import com.es.aplicacion.model.Valoracion
import com.es.aplicacion.service.ValoracionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/valoracion")
class ValoracionController {

    @Autowired
    private lateinit var valoracionService: ValoracionService

    @GetMapping("/{libro_id}")
    fun getValoraciones(
        @PathVariable(value = "libro_id") libroId: String
    ): ResponseEntity<List<Valoracion>?> {
        return ResponseEntity(valoracionService.gatValoraciones(libroId), HttpStatus.OK)
    }

    @PostMapping("/add")
    fun addValoracion(
        @RequestBody valoracion: Valoracion,
        authentication: Authentication
    ): ResponseEntity<String> {
        return ResponseEntity(valoracionService.addValoracion(valoracion),HttpStatus.CREATED)
    }

    @DeleteMapping("/eliminar/{idValoracion}")
    fun removeValoracion(
        @PathVariable(value = "id") idValoracion: String,
        authentication: Authentication
    ): ResponseEntity<String> {
        valoracionService.deleteValoracion(idValoracion,authentication)
        return ResponseEntity.noContent().build()
    }
}