package com.es.aplicacion.controller

import com.es.aplicacion.dto.TareaInsertDTO
import com.es.aplicacion.model.Tarea
import com.es.aplicacion.service.TareaService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/tareas")
class TareaController {

    @Autowired
    private lateinit var tareaService: TareaService

    @GetMapping("/tareas")
    fun getTarea(
        authentication: Authentication
    ): ResponseEntity<List<Tarea>> {
        val tareas = tareaService.getTareaByUsername(authentication)
        return ResponseEntity(tareas, HttpStatus.OK)
    }


    @PostMapping("/tarea")
    fun insertTarea(
        authentication: Authentication,
        @RequestBody tareaInsertDTO: TareaInsertDTO
    ): ResponseEntity<Tarea> {
        val tarea = tareaService.inserirTarea(tareaInsertDTO,authentication)

        return ResponseEntity(tarea,HttpStatus.CREATED)
    }


    @PutMapping("/tarea/{id}")
    fun updateTarea(
        authentication: Authentication,
        @PathVariable("id") id: ObjectId,
        @RequestBody tarea: Tarea
    ): ResponseEntity<Tarea> {
        val nuevaTarea  = tareaService.updateTarea(tarea,authentication)
        return ResponseEntity(nuevaTarea,HttpStatus.OK)
    }



    @DeleteMapping("/tarea/{tarea_id}")
    fun deleteTarea(
        authentication: Authentication,
        @PathVariable tarea_id: ObjectId
    ): ResponseEntity<Boolean> {
        tareaService.deleteTarea(tarea_id,authentication)

        return ResponseEntity.noContent().build()
    }
}