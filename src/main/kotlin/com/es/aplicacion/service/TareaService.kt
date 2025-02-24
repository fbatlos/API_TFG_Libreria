package com.es.aplicacion.service

import com.es.aplicacion.dto.TareaInsertDTO
import com.es.aplicacion.error.exception.Forbidden
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Tarea
import com.es.aplicacion.repository.TareaRepository
import org.apache.coyote.BadRequestException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.List

@Service
class TareaService {

    @Autowired
    private lateinit var tareaRepository: TareaRepository

    fun getTareaByUsername(authentication: Authentication): List<Tarea> {
        if (authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))) {
            val tareas = tareaRepository.findAll()
            return tareas
        }else{
            val tareas = tareaRepository.findTareaByUsername(authentication.name).orElseGet { throw NotFound("No hay tareas con ese usuario.") }

            return tareas
        }
    }

    fun inserirTarea(tareaInsertDTO: TareaInsertDTO,authentication: Authentication): Tarea? {

        if (tareaInsertDTO.titulo.isBlank() || tareaInsertDTO.cuerpo.isBlank()) {throw BadRequestException("Requiere de un titulo y un cuerpo.")}

        if (tareaInsertDTO.username != authentication.name && !authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))) {
            println(authentication.authorities.toString())
            println(authentication.name)
            //TODO CAMBIAR A FORBIDDEN
            throw Forbidden("No estas autorizado.")
        }

        val tarea = Tarea(_id = null,titulo = tareaInsertDTO.titulo, cuerpo = tareaInsertDTO.cuerpo, username = tareaInsertDTO.username, fecha_pub = Date())

        tareaRepository.save(tarea)

        return tarea
    }

    fun deleteTarea(idTarea:ObjectId,authentication: Authentication): Boolean? {
        val tarea = tareaRepository.findById(idTarea.toString()).orElseGet { throw  BadRequestException("No hay tareas con id.") }

        if (authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")) || tarea.username == authentication.name) {
            tareaRepository.deleteById(idTarea.toString())
            return true
        }else{
            throw Forbidden("No estas autorizado.")
        }
    }

    fun updateTarea(tarea:Tarea,authentication: Authentication): Tarea? {
        if (!authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")) && tarea.username != authentication.name) {throw Forbidden("No estas autorizado.")}

        val tareaExistente = tareaRepository.findById(tarea._id!!).orElse(null)
            ?: throw NotFound("Tarea no encontrada.")


        tareaExistente.titulo = tarea.titulo
        tareaExistente.cuerpo = tarea.cuerpo
        tareaExistente.fecha_pub = tarea.fecha_pub
        tareaExistente.completada = tarea.completada


        return tareaRepository.save(tareaExistente)

    }

}