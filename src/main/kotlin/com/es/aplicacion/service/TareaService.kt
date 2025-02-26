package com.es.aplicacion.service

import com.es.aplicacion.dto.TareaInsertDTO
import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Forbidden
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Tarea
import com.es.aplicacion.repository.TareaRepository
import com.es.aplicacion.repository.UsuarioRepository
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

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

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

        if (tareaInsertDTO.titulo.isBlank() || tareaInsertDTO.cuerpo.isBlank()) {throw BadRequest("Requiere de un titulo y un cuerpo.")}

        if(authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))){usuarioRepository.findByUsername(tareaInsertDTO.username).orElseGet { throw BadRequest("El usuario no existe.") }}

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
        val tarea = tareaRepository.findById(idTarea.toString()).orElseGet { throw  BadRequest("No hay tareas con id.") }

        if (authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")) || tarea.username == authentication.name) {
            tareaRepository.deleteById(idTarea.toString())
            return true
        }else{
            throw Forbidden("No estas autorizado.")
        }
    }

    fun updateTarea(tarea:Tarea,authentication: Authentication): Tarea? {
        if (!authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")) && tarea.username != authentication.name) {throw Forbidden("No estas autorizado.")}

        if(authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))){usuarioRepository.findByUsername(tarea.username).orElseGet { throw BadRequest("El usuario no existe.") }}

        val tareaExistente = tareaRepository.findById(tarea._id!!).orElse(null)
            ?: throw NotFound("Tarea no encontrada.")

        if (tarea.titulo.isBlank() || tarea.cuerpo.isBlank() ) {throw BadRequest("El tirulo y el cuerpo son obligatorios.")}


        tareaExistente.titulo = tarea.titulo
        tareaExistente.cuerpo = tarea.cuerpo
        tareaExistente.username = tarea.username
        tareaExistente.fecha_pub = tarea.fecha_pub
        tareaExistente.completada = tarea.completada


        return tareaRepository.save(tareaExistente)

    }

}