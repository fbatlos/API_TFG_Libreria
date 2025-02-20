package com.es.aplicacion.repository

import com.es.aplicacion.model.Tarea
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TareaRepository : MongoRepository<Tarea, String>{
    fun findTareaByUsername(username:String):Optional<List<Tarea>>
}