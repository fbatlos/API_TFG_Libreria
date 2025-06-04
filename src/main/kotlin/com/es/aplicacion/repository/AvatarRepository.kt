package com.es.aplicacion.repository

import com.es.aplicacion.model.Avatar
import com.es.aplicacion.model.Compra
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AvatarRepository: MongoRepository<Avatar, String> {
}